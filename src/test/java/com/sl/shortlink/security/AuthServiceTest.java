package com.sl.shortlink.security;
import com.sl.shortlink.exception.UserAlreadyExistException;
import com.sl.shortlink.model.AppUser;
import com.sl.shortlink.model.security.AuthRequest;
import com.sl.shortlink.model.security.AuthResponse;
import com.sl.shortlink.repo.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
class AuthServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_ShouldReturnAuthResponseWithToken() {
        AuthRequest request = new AuthRequest("user", "pass");

        when(jwtUtil.generateToken("user")).thenReturn("token");

        AuthResponse response = authService.login(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertThat(response.token()).isEqualTo("token");
    }

    @Test
    void registry_ShouldSaveUserAndReturnRegistered() {
        AuthRequest request = new AuthRequest("newuser", "pass");

        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = authService.registry(request);

        verify(appUserRepository).save(argThat(user ->
                user.getUsername().equals("newuser") &&
                        user.getPassword().equals("encodedPass") &&
                        user.getRole().equals("ROLE_USER")
        ));

        assertThat(result).isEqualTo("Registered");
    }

    @Test
    void registry_WhenUsernameExists_ShouldThrowUserAlreadyExistException() {
        AuthRequest request = new AuthRequest("existingUser", "pass");

        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");
        when(appUserRepository.save(any(AppUser.class))).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> authService.registry(request))
                .isInstanceOf(UserAlreadyExistException.class)
                .hasMessageContaining("Username already exist");
    }

    @Test
    void findAppUser_WhenUserExists_ShouldReturnAppUser() {
        AppUser user = new AppUser();
        user.setUsername("user");
        when(appUserRepository.findByUsername("user")).thenReturn(java.util.Optional.of(user));

        AppUser found = authService.findAppUser("user");

        assertThat(found).isEqualTo(user);
    }

    @Test
    void findAppUser_WhenUserNotFound_ShouldThrowUsernameNotFoundException() {
        when(appUserRepository.findByUsername("missing")).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> authService.findAppUser("missing"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }

}
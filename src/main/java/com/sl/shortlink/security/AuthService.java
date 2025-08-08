package com.sl.shortlink.security;

import com.sl.shortlink.exception.UserAlreadyExistException;
import com.sl.shortlink.model.AppUser;
import com.sl.shortlink.model.security.AuthRequest;
import com.sl.shortlink.model.security.AuthResponse;
import com.sl.shortlink.repo.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(AuthRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = jwtUtil.generateToken((request.getUsername()));
        return  new AuthResponse(token);
    }

    public String registry(AuthRequest request){
        AppUser user = new AppUser();
        user.setId(null);
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");
        try{
            appUserRepository.save(user);
        }catch (DataIntegrityViolationException e){
            throw new UserAlreadyExistException("Username already exist" + user.getUsername());
        }
        return "Registered";

    }

    public AppUser findAppUser(String username) {
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}

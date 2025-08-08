package com.sl.shortlink.service.impl;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.sl.shortlink.dto.ShortenResponse;
import com.sl.shortlink.event.UrlAccessedEvent;
import com.sl.shortlink.exception.SaveFailException;
import com.sl.shortlink.exception.UrlNotFoundException;
import com.sl.shortlink.model.AnalyticsDto;
import com.sl.shortlink.model.AppUser;
import com.sl.shortlink.model.UrlMapping;
import com.sl.shortlink.repo.UrlShortenerRepo;
import com.sl.shortlink.security.AuthService;
import com.sl.shortlink.service.ShortCodeGenerator;
import com.sl.shortlink.validator.CustomCodeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

class UrlShortenerServiceImplTest {


    private UrlShortenerRepo repo;
    private ShortCodeGenerator codeGenerator;
    private ApplicationEventPublisher eventPublisher;
    private AuthService authService;
    private CustomCodeValidator customCodeValidator;
    private ModelMapper modelMapper;
    private UrlShortenerServiceImpl service;

    @BeforeEach
    void setup() {
        repo = mock(UrlShortenerRepo.class);
        codeGenerator = mock(ShortCodeGenerator.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        authService = mock(AuthService.class);
        customCodeValidator = mock(CustomCodeValidator.class);
        modelMapper = new ModelMapper();

        service = new UrlShortenerServiceImpl(repo, codeGenerator, eventPublisher, authService, customCodeValidator, modelMapper);

        service.setUrlCodeLength(6);
        service.setDomain("http://short.ly/");
    }

    private void mockSecurityContext(String username) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(username);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void getOriginalUrl_ShouldReturnOriginalUrl_AndPublishEvent_WhenUserHasAccess() {
        String username = "user1";
        String shortCode = "abc123";
        UrlMapping mapping = new UrlMapping();
        mapping.setOriginalUrl("http://example.com");
        AppUser user = new AppUser();
        user.setUsername(username);
        mapping.setAppUser(user);

        mockSecurityContext(username);
        when(repo.findByShortCode(shortCode)).thenReturn(Optional.of(mapping));

        String originalUrl = service.getOriginalUrl(shortCode);

        assertEquals("http://example.com", originalUrl);
        verify(eventPublisher, times(1)).publishEvent(any(UrlAccessedEvent.class));
    }

    @Test
    void getOriginalUrl_ShouldThrowAccessDeniedException_WhenUserHasNoAccess() {
        String username = "user1";
        String shortCode = "abc123";
        UrlMapping mapping = new UrlMapping();
        AppUser user = new AppUser();
        user.setUsername("otherUser");
        mapping.setAppUser(user);

        mockSecurityContext(username);
        when(repo.findByShortCode(shortCode)).thenReturn(Optional.of(mapping));

        assertThrows(AccessDeniedException.class, () -> service.getOriginalUrl(shortCode));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void getOriginalUrl_ShouldThrowUrlNotFoundException_WhenCodeNotFound() {
        mockSecurityContext("user1");
        when(repo.findByShortCode("abc123")).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> service.getOriginalUrl("abc123"));
    }

    @Test
    void shorten_ShouldSaveUrlMapping_WhenCustomCodeIsProvided() {
        String url = "http://example.com";
        String customCode = "custom1";
        String username = "user1";

        mockSecurityContext(username);
        AppUser user = new AppUser();
        user.setUsername(username);
        when(authService.findAppUser(username)).thenReturn(user);

        doNothing().when(customCodeValidator).validateCustomCode(customCode);
        doNothing().when(customCodeValidator).validIfCodeExist(customCode);

        when(repo.save(any(UrlMapping.class))).thenAnswer(i -> i.getArgument(0));

        ShortenResponse response = service.shorten(url, customCode);

        assertTrue(response.getShortenedUrl().contains(customCode));
        verify(repo).save(any(UrlMapping.class));
    }

    @Test
    void shorten_ShouldGenerateCode_WhenCustomCodeIsNull() {
        String url = "http://example.com";
        String generatedCode = "gen123";
        String username = "user1";

        mockSecurityContext(username);
        AppUser user = new AppUser();
        user.setUsername(username);
        when(authService.findAppUser(username)).thenReturn(user);

        when(codeGenerator.generateCode(6)).thenReturn(generatedCode);
        when(repo.existsByShortCode(generatedCode)).thenReturn(false);

        doNothing().when(customCodeValidator).validateCustomCode(generatedCode);
        doNothing().when(customCodeValidator).validIfCodeExist(generatedCode);

        when(repo.save(any(UrlMapping.class))).thenAnswer(i -> i.getArgument(0));

        ShortenResponse response = service.shorten(url, null);

        assertTrue(response.getShortenedUrl().contains(generatedCode));
        verify(repo).save(any(UrlMapping.class));
    }

    @Test
    void shorten_ShouldThrowSaveFailException_WhenSaveFails() {
        String url = "http://example.com";
        String customCode = "custom1";
        String username = "user1";

        mockSecurityContext(username);
        AppUser user = new AppUser();
        user.setUsername(username);
        when(authService.findAppUser(username)).thenReturn(user);

        doNothing().when(customCodeValidator).validateCustomCode(customCode);
        doNothing().when(customCodeValidator).validIfCodeExist(customCode);

        when(repo.save(any(UrlMapping.class))).thenThrow(new RuntimeException("db error"));

        assertThrows(SaveFailException.class, () -> service.shorten(url, customCode));
    }

    @Test
    void analytics_ShouldReturnListOfAnalyticsDto() {
        String username = "user1";
        mockSecurityContext(username);

        AppUser user = new AppUser();
        user.setUsername(username);

        UrlMapping mapping = new UrlMapping();
        mapping.setOriginalUrl("http://example.com");
        mapping.setShortCode("abc123");
        mapping.setClickCount(10);
        mapping.setAppUser(user);

        when(repo.findByAppUser_Username(username)).thenReturn(List.of(mapping));

        List<AnalyticsDto> analytics = service.analytics();

        assertEquals(1, analytics.size());
        AnalyticsDto dto = analytics.get(0);
        assertEquals("http://example.com", dto.getOriginalUrl());
        assertEquals("abc123", dto.getShortCode());
        assertEquals(10, dto.getClickCount());
    }

}
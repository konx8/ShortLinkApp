package com.sl.shortlink.service.impl;


import com.sl.shortlink.dto.ShortenResponse;
import com.sl.shortlink.event.UrlAccessedEvent;
import com.sl.shortlink.exception.InvalidShortCodeException;
import com.sl.shortlink.exception.SaveFailException;
import com.sl.shortlink.exception.UrlNotFoundException;
import com.sl.shortlink.model.AppUser;
import com.sl.shortlink.model.UrlMapping;
import com.sl.shortlink.repo.UrlShortenerRepo;
import com.sl.shortlink.security.AuthService;
import com.sl.shortlink.service.ShortCodeGenerator;
import com.sl.shortlink.service.UrlShortenerService;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UrlShortenerServiceImpl implements UrlShortenerService {

    @Value("${url.domain}")
    private String domain;

    @Value("${url.code-length}")
    private int urlCodeLength;

    private final UrlShortenerRepo urlShortenerRepo;

    private final ShortCodeGenerator shortCodeGenerator;

    private final ApplicationEventPublisher eventPublisher;

    private final AuthService authService;

    public UrlShortenerServiceImpl(UrlShortenerRepo urlShortenerRepo, ShortCodeGenerator shortCodeGenerator,
                                   ApplicationEventPublisher eventPublisher, AuthService authService) {
        this.urlShortenerRepo = urlShortenerRepo;
        this.shortCodeGenerator = shortCodeGenerator;
        this.eventPublisher = eventPublisher;
        this.authService = authService;
    }

    @Override
    public String getOriginalUrl(String shortCode) {
        validShortCode(shortCode);
        UrlMapping urlByCode = findUrlByCode(shortCode);
        String username = getUSernameFroSecurityContext();
        if (urlByCode.getAppUser() == null || !username.equals(urlByCode.getAppUser().getUsername())) {
            throw new AccessDeniedException(username + " don't have access to this resource");
        }
        eventPublisher.publishEvent(new UrlAccessedEvent(urlByCode));

        return urlByCode.getOriginalUrl();

    }

    @Override
    public ShortenResponse shorten(@NotBlank @URL String url) {
        UrlMapping urlMapping = new UrlMapping();
        String generateCode = generateCode();
        String username = getUSernameFroSecurityContext();
        AppUser appUser = authService.findAppUser(username);

        urlMapping.setOriginalUrl(url);
        urlMapping.setShortCode(generateCode);
        urlMapping.setAppUser(appUser);

        try {
            urlShortenerRepo.save(urlMapping);
            log.info("User '{}' created short link: {}", username, generateCode);
        } catch (Exception e) {
            throw new SaveFailException("Failed to save URL", e);
        }
        return new ShortenResponse(domain + generateCode);
    }

    private String getUSernameFroSecurityContext() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private UrlMapping findUrlByCode(String shortCode) {
        return urlShortenerRepo.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Url not found for code: " + shortCode));
    }

    private void validShortCode(String shortCode) {
        if (shortCode.length() != urlCodeLength) {
            throw new InvalidShortCodeException("Invalid short code: " + shortCode);
        }
    }

    private String generateCode() {
        String generateCode;
        do {
            generateCode = shortCodeGenerator.generateCode(urlCodeLength);
        } while (urlShortenerRepo.existsByShortCode(generateCode));
        return generateCode;
    }

}

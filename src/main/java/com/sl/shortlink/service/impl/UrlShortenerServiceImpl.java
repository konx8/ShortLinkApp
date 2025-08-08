package com.sl.shortlink.service.impl;


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
import com.sl.shortlink.service.UrlShortenerService;
import com.sl.shortlink.validator.CustomCodeValidator;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.URL;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;


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

    private final CustomCodeValidator customCodeValidator;

    private final ModelMapper modelMapper;

    public UrlShortenerServiceImpl(UrlShortenerRepo urlShortenerRepo, ShortCodeGenerator shortCodeGenerator,
                                   ApplicationEventPublisher eventPublisher, AuthService authService,
                                   CustomCodeValidator customCodeValidator, ModelMapper modelMapper) {
        this.urlShortenerRepo = urlShortenerRepo;
        this.shortCodeGenerator = shortCodeGenerator;
        this.eventPublisher = eventPublisher;
        this.authService = authService;
        this.customCodeValidator = customCodeValidator;
        this.modelMapper = modelMapper;
    }

    @Override
    public String getOriginalUrl(String shortCode) {
        UrlMapping urlByCode = findUrlByCode(shortCode);
        String username = getUsernameFroSecurityContext();
        if (urlByCode.getAppUser() == null || !username.equals(urlByCode.getAppUser().getUsername())) {
            throw new AccessDeniedException(username + " don't have access to this resource");
        }
        eventPublisher.publishEvent(new UrlAccessedEvent(urlByCode));

        return urlByCode.getOriginalUrl();

    }

    @Override
    public ShortenResponse shorten(@NotBlank @URL String url, String customCode) {

        String finalCode = customCode != null && !customCode.isBlank() ? customCode : generateCode();

        customCodeValidator.validateCustomCode(finalCode);
        customCodeValidator.validIfCodeExist(finalCode);
        String username = getUsernameFroSecurityContext();
        AppUser appUser = authService.findAppUser(username);

        UrlMapping urlMapping = new UrlMapping();

        urlMapping.setOriginalUrl(url);
        urlMapping.setShortCode(finalCode);
        urlMapping.setAppUser(appUser);

        try {
            urlShortenerRepo.save(urlMapping);
            log.info("User '{}' created short link: {}", username, finalCode);
        } catch (Exception e) {
            throw new SaveFailException("Failed to save URL", e);
        }
        return new ShortenResponse(domain + finalCode);
    }

    @Override
    public List<AnalyticsDto> analytics() {
        String username = getUsernameFroSecurityContext();
        List<UrlMapping> userUrlList = urlShortenerRepo.findByAppUser_Username(username);

        List<AnalyticsDto> analyticsDtoList = userUrlList.stream()
                .map(singleUrl -> modelMapper.map(singleUrl, AnalyticsDto.class))
                .toList();

        return analyticsDtoList;
    }

    private String getUsernameFroSecurityContext() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private UrlMapping findUrlByCode(String shortCode) {
        return urlShortenerRepo.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Url not found for code: " + shortCode));
    }

    private String generateCode() {
        String generateCode;
        do {
            generateCode = shortCodeGenerator.generateCode(urlCodeLength);
        } while (urlShortenerRepo.existsByShortCode(generateCode));
        return generateCode;
    }

}

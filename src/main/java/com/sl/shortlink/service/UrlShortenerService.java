package com.sl.shortlink.service;

import com.sl.shortlink.dto.ShortenResponse;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public interface UrlShortenerService {

    ShortenResponse shorten(@NotBlank @URL String url);

    String getOriginalUrl(String shortCode);

}

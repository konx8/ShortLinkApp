package com.sl.shortlink.service;

import com.sl.shortlink.dto.ShortenResponse;
import com.sl.shortlink.model.AnalyticsDto;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import java.util.List;

public interface UrlShortenerService {

    ShortenResponse shorten(@NotBlank @URL String url, String customCode);

    String getOriginalUrl(String shortCode);

    List<AnalyticsDto> analytics();

}

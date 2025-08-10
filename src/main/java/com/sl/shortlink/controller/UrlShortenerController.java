package com.sl.shortlink.controller;

import com.sl.shortlink.dto.ShortenRequest;
import com.sl.shortlink.dto.ShortenResponse;
import com.sl.shortlink.model.AnalyticsDto;
import com.sl.shortlink.service.UrlShortenerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> shortenUrl(@Valid @RequestBody ShortenRequest request) {
        return ResponseEntity.ok(urlShortenerService.shorten(request.getUrl(), request.getCustomCode()));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Map<String, String>> getOriginalUrl(@PathVariable String shortCode) {
        String originalUrl = urlShortenerService.getOriginalUrl(shortCode);
        return ResponseEntity.ok(Collections.singletonMap("url", originalUrl));
    }

    @GetMapping("/analytics")
    public ResponseEntity<List<AnalyticsDto>> analytics() {
        return ResponseEntity.ok(urlShortenerService.analytics());
    }

}

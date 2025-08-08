package com.sl.shortlink.controller;

import com.sl.shortlink.dto.ShortenRequest;
import com.sl.shortlink.dto.ShortenResponse;
import com.sl.shortlink.model.AnalyticsDto;
import com.sl.shortlink.service.UrlShortenerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@AllArgsConstructor
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> shortenUrl(@Valid @RequestBody ShortenRequest request) {
        return ResponseEntity.ok(urlShortenerService.shorten(request.getUrl(), request.getCustomCode()));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginal(@PathVariable String shortCode) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(urlShortenerService.getOriginalUrl(shortCode)))
                .build();
    }

    @GetMapping("/analytics")
    public ResponseEntity<List<AnalyticsDto>> analytics() {
        return ResponseEntity.ok(urlShortenerService.analytics());
    }

}

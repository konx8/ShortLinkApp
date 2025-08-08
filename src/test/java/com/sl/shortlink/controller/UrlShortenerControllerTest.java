package com.sl.shortlink.controller;

import com.sl.shortlink.dto.ShortenRequest;
import com.sl.shortlink.dto.ShortenResponse;
import com.sl.shortlink.model.AnalyticsDto;
import com.sl.shortlink.service.UrlShortenerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShortenerControllerTest {

    @Mock
    private UrlShortenerService urlShortenerService;

    @InjectMocks
    private UrlShortenerController controller;

    @Test
    void shortenUrl_ReturnsShortenResponse() {
        ShortenRequest request = new ShortenRequest();
        request.setUrl("http://example.com");
        request.setCustomCode("abc123");

        ShortenResponse response = new ShortenResponse("http://short.url/abc123");

        when(urlShortenerService.shorten(request.getUrl(), request.getCustomCode())).thenReturn(response);

        ResponseEntity<ShortenResponse> result = controller.shortenUrl(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);

        verify(urlShortenerService).shorten(request.getUrl(), request.getCustomCode());
    }

    @Test
    void redirectToOriginal_ReturnsFoundResponseWithLocation() {
        String shortCode = "abc123";
        String originalUrl = "http://example.com";

        when(urlShortenerService.getOriginalUrl(shortCode)).thenReturn(originalUrl);

        ResponseEntity<Void> response = controller.redirectToOriginal(shortCode);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create(originalUrl));

        verify(urlShortenerService).getOriginalUrl(shortCode);
    }

    @Test
    void analytics_ReturnsListOfAnalyticsDto() {
        List<AnalyticsDto> analyticsList = List.of(
                new AnalyticsDto("www.test.pl", "abc123", 5),
                new AnalyticsDto("www.test.pl", "def456", 10)
        );

        when(urlShortenerService.analytics()).thenReturn(analyticsList);

        ResponseEntity<List<AnalyticsDto>> response = controller.analytics();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(analyticsList);

        verify(urlShortenerService).analytics();
    }

}
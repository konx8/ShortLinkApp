package com.sl.shortlink.event;

import com.sl.shortlink.model.UrlMapping;
import com.sl.shortlink.repo.UrlShortenerRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UrlAccessedEventListenerTest {

    private UrlShortenerRepo urlShortenerRepo;
    private UrlAccessedEventListener listener;

    @BeforeEach
    void setUp() {
        urlShortenerRepo = mock(UrlShortenerRepo.class);
        listener = new UrlAccessedEventListener(urlShortenerRepo);
    }

    @Test
    void shouldIncrementClickCountAndSave() {
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setClickCount(5);

        UrlAccessedEvent event = new UrlAccessedEvent(urlMapping);

        listener.onUrlAccessed(event);

        ArgumentCaptor<UrlMapping> captor = ArgumentCaptor.forClass(UrlMapping.class);
        verify(urlShortenerRepo).save(captor.capture());

        UrlMapping saved = captor.getValue();
        assertEquals(6, saved.getClickCount());
    }

}
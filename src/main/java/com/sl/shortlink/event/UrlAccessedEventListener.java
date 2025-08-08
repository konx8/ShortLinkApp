package com.sl.shortlink.event;

import com.sl.shortlink.model.UrlMapping;
import com.sl.shortlink.repo.UrlShortenerRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UrlAccessedEventListener {

    private final UrlShortenerRepo urlShortenerRepo;

    @Transactional
    @EventListener
    public void onUrlAccessed(UrlAccessedEvent event){
        UrlMapping urlMapping = event.urlMapping();
        urlMapping.setClickCount(urlMapping.getClickCount() + 1);
        urlShortenerRepo.save(urlMapping);
    }


}

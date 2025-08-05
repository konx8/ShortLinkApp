package com.sl.shortlink.event;

import com.sl.shortlink.model.UrlMapping;

public record UrlAccessedEvent(UrlMapping urlMapping) {

}

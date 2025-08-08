package com.sl.shortlink.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AnalyticsDto {

    private String originalUrl;
    private String shortCode;
    private int clickCount;

}

package com.sl.shortlink.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
public class ShortenRequest {

    @NotBlank
    @URL
    private String url;

}

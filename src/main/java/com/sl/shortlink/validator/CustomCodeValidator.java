package com.sl.shortlink.validator;

import com.sl.shortlink.exception.InvalidShortCodeException;
import com.sl.shortlink.exception.ShortCodeAlreadyExistsException;
import com.sl.shortlink.repo.UrlShortenerRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@AllArgsConstructor
public class CustomCodeValidator {

    private final UrlShortenerRepo urlShortenerRepo;

    public void validIfCodeExist(String finalCode) {
        if(urlShortenerRepo.existsByShortCode(finalCode)) {
            throw new ShortCodeAlreadyExistsException("Short code already in use: " + finalCode);
        }
    }

    public void validateCustomCode(String customCode){
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]{4,20}$");
        if (!pattern.matcher(customCode).matches()) {
            throw new InvalidShortCodeException("Custom code must be 4-20 characters long and contain only letters, digits, '-' or '_'");
        }
    }

}

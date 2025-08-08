package com.sl.shortlink.validator;


import com.sl.shortlink.exception.InvalidShortCodeException;
import com.sl.shortlink.exception.ShortCodeAlreadyExistsException;
import com.sl.shortlink.repo.UrlShortenerRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomCodeValidatorTest {

    private UrlShortenerRepo urlShortenerRepo;
    private CustomCodeValidator validator;

    @BeforeEach
    void setUp() {
        urlShortenerRepo = mock(UrlShortenerRepo.class);
        validator = new CustomCodeValidator(urlShortenerRepo);
    }

    @Test
    void validIfCodeExist_CodeExists_ThrowsException() {
        String code = "abc123";
        when(urlShortenerRepo.existsByShortCode(code)).thenReturn(true);

        ShortCodeAlreadyExistsException ex = assertThrows(ShortCodeAlreadyExistsException.class, () -> validator.validIfCodeExist(code));
        assertTrue(ex.getMessage().contains(code));

        verify(urlShortenerRepo, times(1)).existsByShortCode(code);
    }

    @Test
    void validIfCodeExist_CodeDoesNotExist_DoesNotThrow() {
        String code = "abc123";
        when(urlShortenerRepo.existsByShortCode(code)).thenReturn(false);

        assertDoesNotThrow(() -> validator.validIfCodeExist(code));
        verify(urlShortenerRepo, times(1)).existsByShortCode(code);
    }

    @Test
    void validateCustomCode_ValidCode_DoesNotThrow() {
        String validCode = "valid_Code123-";

        assertDoesNotThrow(() -> validator.validateCustomCode(validCode));
    }

    @Test
    void validateCustomCode_InvalidCode_ThrowsException() {
        String[] invalidCodes = {
                "abc",
                "a@bcde",
                "thisIsAVeryLongCodeThatExceeds20Chars",
                "code with spaces",
                "code$with$specials!"
        };

        for (String code : invalidCodes) {
            InvalidShortCodeException ex = assertThrows(InvalidShortCodeException.class, () -> validator.validateCustomCode(code));
            assertTrue(ex.getMessage().contains("Custom code must be 4-20 characters"));
        }
    }

}
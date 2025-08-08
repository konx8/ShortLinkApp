package com.sl.shortlink.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Base62ShortCodeGeneratorTest {

    private Base62ShortCodeGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new Base62ShortCodeGenerator();
    }

    @Test
    void generateCode_ShouldReturnCodeOfRequestedLength_WhenLengthIsLessThanGenerated() {
        int length = 8;
        String code = generator.generateCode(length);
        assertNotNull(code);
        assertEquals(length, code.length());
    }

    @Test
    void generateCode_ShouldReturnCodeShorterOrEqualToLength_WhenGeneratedCodeIsShort() {
        int length = 30;
        String code = generator.generateCode(length);
        assertNotNull(code);
        assertTrue(code.length() <= length);
        assertFalse(code.isEmpty());
    }

    @RepeatedTest(10)
    void generateCode_ShouldOnlyContainBase62Characters() {
        int length = 16;
        String code = generator.generateCode(length);
        assertTrue(code.matches("[0-9a-zA-Z]+"));
    }

    @Test
    void generateCode_ShouldGenerateDifferentCodes() {
        int length = 12;
        String code1 = generator.generateCode(length);
        String code2 = generator.generateCode(length);
        assertNotEquals(code1, code2);
    }

}
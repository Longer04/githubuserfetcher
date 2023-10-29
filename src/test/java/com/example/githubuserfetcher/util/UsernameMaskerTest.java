package com.example.githubuserfetcher.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UsernameMaskerTest {

    @Test
    public void maskUsernameShouldMaskAllButFirstCharacter() {
        final String originalUsername = "username";
        final String expectedMaskedUsername = "u*******";
        assertEquals(expectedMaskedUsername, UsernameMasker.maskUsername(originalUsername));
    }

    @Test
    public void maskUsernameShouldHandleSingleCharacter() {
        final String originalUsername = "u";
        final String expectedMaskedUsername = "u";
        assertEquals(expectedMaskedUsername, UsernameMasker.maskUsername(originalUsername));
    }

    @Test
    public void maskUsernameShouldHandleEmptyString() {
        final String originalUsername = "";
        final String expectedMaskedUsername = "";
        assertEquals(expectedMaskedUsername, UsernameMasker.maskUsername(originalUsername));
    }

    @Test
    public void maskUsernameShouldHandleNullInput() {
        assertNull(UsernameMasker.maskUsername(null));
    }

    @Test
    public void maskUsernameShouldHandleSpecialCharacters() {
        final String originalUsername = "user!";
        final String expectedMaskedUsername = "u****";
        assertEquals(expectedMaskedUsername, UsernameMasker.maskUsername(originalUsername));
    }

    @Test
    public void maskUsernameShouldHandleNumericalCharacters() {
        final String originalUsername = "user123";
        final String expectedMaskedUsername = "u******";
        assertEquals(expectedMaskedUsername, UsernameMasker.maskUsername(originalUsername));
    }
}
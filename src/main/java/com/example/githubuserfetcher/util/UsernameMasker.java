package com.example.githubuserfetcher.util;


public final class UsernameMasker {

    public static final String THIS_IS_A_UTILITY_CLASS_AND_CANNOT_BE_INSTANTIATED = "This is a utility class and cannot be instantiated";

    private UsernameMasker() {
        throw new UnsupportedOperationException(THIS_IS_A_UTILITY_CLASS_AND_CANNOT_BE_INSTANTIATED);
    }

    public static String maskUsername(final String username) {
        if (username == null || username.isEmpty()) {
            return username;
        }
        return username.charAt(0) + username.substring(1).replaceAll("\\.", "*");
    }
}
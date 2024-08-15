package com.matchify.utils;

public class TestUtil {
    private static final String BASE_EMAIL = "vaibhav";
    private static int emailCounter = 1;

    public static String generateUniqueEmail() {
        String email = BASE_EMAIL + emailCounter + "@dal.ca";
        emailCounter++;
        return email;
    }
}

package com.matchify.config;

public class JwtConstant {

    public static final String SECRET_KEY="5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    public static final String JWT_HEADER="Authorization";

    public static final long TOKEN_EXPIRY_IN_MS = 1000L * 60 * 60 * 24 * 30; // 30 days
    public static final int BEARER_LENGTH = 7;
}


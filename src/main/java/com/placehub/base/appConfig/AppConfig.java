package com.placehub.base.appConfig;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    public final class Constraints {
        public static final int USERNAME_MIN_LENGTH = 4;
        public static final int USERNAME_MAX_LENGTH = 20;
        public static final int PASSWORD_MIN_LENGTH = 4;
        public static final int PASSWORD_MAX_LENGTH = 20;

        public static final int NICKNAME_MIN_LENGTH = 2;
        public static final int NICKNAME_MAX_LENGTH = 20;
    }
}
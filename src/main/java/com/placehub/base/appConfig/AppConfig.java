package com.placehub.base.appConfig;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

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
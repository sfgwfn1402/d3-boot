package com.dddframework.web.utils;

import org.springframework.format.Formatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateTimeFormatter implements Formatter<LocalDateTime> {
    public static final DateTimeFormatter FORMATTER;

    public LocalDateTime parse(String text, Locale locale) {
        return LocalDateTime.parse(text, FORMATTER);
    }

    public String print(LocalDateTime object, Locale locale) {
        return FORMATTER.format(object);
    }

    static {
        FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
    }
}

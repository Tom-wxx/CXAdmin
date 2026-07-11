package com.admin.system.enums;

import java.util.Arrays;

public enum LoginPetType {
    CAT("cat"),
    DOG("dog"),
    OWL("owl");

    public static final String DEFAULT_VALUE = "cat";

    private final String value;

    LoginPetType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isValid(String value) {
        return Arrays.stream(values()).anyMatch(type -> type.value.equals(value));
    }

    public static String normalize(String value) {
        return isValid(value) ? value : DEFAULT_VALUE;
    }
}

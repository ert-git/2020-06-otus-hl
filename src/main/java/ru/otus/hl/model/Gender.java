package ru.otus.hl.model;

import lombok.Getter;

public enum Gender {
    MALE(1), FEMALE(0), NOT_SET(2);

    @Getter
    private final int value;

    Gender(int value) {
        this.value = value;
    }

    public static Gender fromValue(Integer value) {
        if (value == null) {
            return NOT_SET;
        }
        for (Gender g : Gender.values()) {
            if (g.getValue() == value) {
                return g;
            }
        }
        return NOT_SET;
    }
}

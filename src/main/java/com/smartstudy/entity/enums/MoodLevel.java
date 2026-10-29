package com.smartstudy.entity.enums;

public enum MoodLevel {
    VERY_LOW(1, "Very Low / Depressed"),
    LOW(2, "Low / Anxious"),
    NEUTRAL(3, "Neutral / Okay"),
    GOOD(4, "Good / Positive"),
    EXCELLENT(5, "Excellent / Motivated");

    private final int value;
    private final String description;

    MoodLevel(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static MoodLevel fromValue(int val) {
        for (MoodLevel mood : values()) {
            if (mood.value == val) {
                return mood;
            }
        }
        return NEUTRAL;
    }
}

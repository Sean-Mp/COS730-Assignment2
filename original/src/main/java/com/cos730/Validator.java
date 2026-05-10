package com.cos730;

public class Validator {

    public String validateFormat(String title, String researcher, String keywords, String abstractText) {
        if (isBlank(title)) {
            return "Research title is required.";
        }

        if (isBlank(researcher)) {
            return "Researcher name is required.";
        }

        if (isBlank(keywords)) {
            return "At least one keyword is required.";
        }

        if (isBlank(abstractText)) {
            return "Abstract is required.";
        }

        if (title.length() < 5) {
            return "Research title must be at least 5 characters long.";
        }

        if (!researcher.matches("[A-Za-z ]+")) {
            return "Researcher name may only contain letters and spaces.";
        }

        if (keywords.length() < 3) {
            return "Keywords must be more descriptive.";
        }

        if (abstractText.length() < 20) {
            return "Abstract must be at least 20 characters long.";
        }

        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

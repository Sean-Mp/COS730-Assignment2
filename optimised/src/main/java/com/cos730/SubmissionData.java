package com.cos730;

public class SubmissionData {
    private final String title;
    private final String researcher;
    private final String keywords;
    private final String abstractText;

    public SubmissionData(String title, String researcher, String keywords, String abstractText) {
        this.title = title;
        this.researcher = researcher;
        this.keywords = keywords;
        this.abstractText = abstractText;
    }

    public String getTitle() {
        return title;
    }

    public String getResearcher() {
        return researcher;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getAbstractText() {
        return abstractText;
    }
}

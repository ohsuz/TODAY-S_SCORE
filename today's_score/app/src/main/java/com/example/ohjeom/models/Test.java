package com.example.ohjeom.models;

public class Test {
    private static Template template;
    private String testName;
    private String score ;

    public Test(String testName) {
        this.testName = testName;
        score = "미채점"; // 모든 테스트 점수를 0으로 초기화
    }

    public static void setTemplate(Template template) {
        Test.template = template;
    }

    public static Template getTemplate() {
        return template;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}

package com.entreprisecorp.proximityv2.questions;

public class QuestionResult {
    String questionText;
    boolean response;


    public QuestionResult(String questionText, boolean response) {
        this.questionText = questionText;
        this.response = response;
    }

    public QuestionResult() {
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "QuestionResult{" +
                "questionText='" + questionText + '\'' +
                ", response=" + response +
                '}';
    }
}

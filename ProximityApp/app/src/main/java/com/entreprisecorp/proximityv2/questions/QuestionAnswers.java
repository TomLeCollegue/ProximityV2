package com.entreprisecorp.proximityv2.questions;

import java.util.ArrayList;

public class QuestionAnswers {

    private ArrayList<uuidAnswer> answers = new ArrayList<uuidAnswer>();

    private String uuid;

    public ArrayList<uuidAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<uuidAnswer> answers) {
        this.answers = answers;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public QuestionAnswers(ArrayList<uuidAnswer> answers, String uuid) {
        this.answers = answers;
        this.uuid = uuid;
    }

    public QuestionAnswers() {
    }

    @Override
    public String toString() {
        return "QuestionAnswers{" +
                "answers=" + answers +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}

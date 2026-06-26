package com.example.plagiarismchecker.dto;

public class PlagiarismRequest {

    private String text1;
    private String text2;
    private Long userId;

    public PlagiarismRequest() {
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
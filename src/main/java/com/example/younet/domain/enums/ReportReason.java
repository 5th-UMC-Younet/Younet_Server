package com.example.younet.domain.enums;


public enum ReportReason {
    DEAL("외부 거래를 요청했어요."),
    MANNER("비매너 사용자에요."),
    CURSE("욕설을 해요."),
    SEXUAL("성희롱을 해요."),
    FRAUD("사기를 당했어요."),
    OTHER("다른 문제가 있어요.")
    ;

    private final String label;

    ReportReason(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
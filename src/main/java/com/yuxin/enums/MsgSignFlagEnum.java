package com.yuxin.enums;

public enum  MsgSignFlagEnum {

    UNSIGN(0, "未签收"),
    SIGNED(1,"已签收");

    MsgSignFlagEnum(Integer type, String content) {
        this.type = type;
        this.content = content;
    }

    private final Integer type;
    private final String content;

    public Integer getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}

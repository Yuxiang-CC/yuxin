package com.yuxin.enums;

public enum YuixnResultEnum {

    USERS_NAME_AND_PWD_IS_NULL(410, "用户名或密码为空"),
    USERS_NAME_AND_PWD_ERROR(411, "用户名或密码不正确"),
    USERS_NAME_IS_NULL(412, "用户名为空"),
    IS_NULL(413, "内容为空"),
    VALUE_ERROR(414, "传入值错误"),
    OK(200, "操作成功")

    ;



    YuixnResultEnum(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    private Integer status;
    private String message;
}

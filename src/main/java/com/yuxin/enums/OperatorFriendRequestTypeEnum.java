package com.yuxin.enums;

import java.util.Arrays;

public enum OperatorFriendRequestTypeEnum {
    IGNORE(0, "忽略"),
    PASS(1, "通过");

    OperatorFriendRequestTypeEnum(Integer type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public static boolean getType(Integer type) {
        boolean result = Arrays.stream(OperatorFriendRequestTypeEnum.values())
                .anyMatch(operatorFriendRequestTypeEnum -> {
                    if (operatorFriendRequestTypeEnum.type == type) {
                        return true;
                    } else {
                        System.out.println("false" + type);
                        return false;
                    }
                });
        return result;
    }

    private Integer type;
    private String msg;

    public Integer getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }
}

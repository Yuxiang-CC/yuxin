package com.yuxin.enums;

import java.util.Arrays;

public enum SearchFriendsStatusEnum {
    OK(200,"OK"),
    NOT_YOURSELF(2,"不能添加自己"),
    FRIENDS_ALLREADLY(3,"不能重复添加好友"),
    USER_NOT_EXIST(4,"用户不存在"),

    ;

    public static String getMessageByKey(Integer status) {
        SearchFriendsStatusEnum[] values = SearchFriendsStatusEnum.values();
        return Arrays.stream(values).filter(searchFriendsStatusEnum -> searchFriendsStatusEnum.getStatus() == status)
                .findFirst().get().message;
    }

    SearchFriendsStatusEnum(Integer status, String message) {
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

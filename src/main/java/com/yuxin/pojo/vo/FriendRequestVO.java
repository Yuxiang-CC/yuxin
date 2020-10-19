package com.yuxin.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestVO {
    private String sendUserId;

    private String sendUsername;

    private String sendFaceImage;

    private String sendNickname;

}

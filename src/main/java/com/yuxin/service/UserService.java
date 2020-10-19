package com.yuxin.service;

import com.yuxin.netty.ChatMsg;
import com.yuxin.pojo.Users;
import com.yuxin.pojo.vo.FriendRequestVO;
import com.yuxin.pojo.vo.MyFriendsVO;

import java.util.List;

public interface UserService {

    /**
     * 判断用户名是否存在
     * @param username
     * @return
     */
    boolean queryUsernameIsExist(String username);

    /**
     * 查询用户是否存在
     * @param username
     * @param password
     * @return
     */
    Users queryUserForLogin(String username, String password);

    /**
     * 添加用户
     * @param user
     * @return
     */
    Users saveUser(Users user);

    /**
     * 修改用户记录
     */
    Users updateUserInfo(Users user);

    /**
     * 搜索用户的前置条件
     * @param myUserId
     * @param friendUsername
     * @return
     */
    Integer preconditionSearchFriends(String myUserId, String friendUsername);

    /**
     * 根据用户查询对象
     * @param username
     * @return
     */
    Users queryUserInfoByUsername(String username);

    /**
     * 发送添加朋友的信息
     * @param myUserId
     * @param friendUsername
     */
    void sendFriendRequest(String myUserId, String friendUsername);

    
    /**
     * 查询添加好友的申请
     * @param acceptUserId
     * @return
     */
    List<FriendRequestVO> queryFriendRequestList(String acceptUserId);

    /**
     *  删除好友请求记录
     * @param sendUserId
     * @param acceptUserId
     */
    public void deleteFriendRequest(String sendUserId, String acceptUserId);

    /**
     * 通过好友请求
     *      1.保存好友
     *      2.逆向保存好友
     *      3.删除好友请求记录
     * @param sendUserId
     * @param acceptUserId
     */
    public void passFriendRequest(String sendUserId, String acceptUserId);

    /**
     * 根据传入用户id 查询好友列表
     * @param userId
     * @return
     */
    List<MyFriendsVO> queryMyFriends(String userId);

    /**
     * 保存聊天到数据库
     * @param chatMsg
     * @return
     */
    String saveMsg(ChatMsg chatMsg);

    /**
     * 批量签收消息
     * @param msgIdList
     */
    void updateMsgSigned(List<String> msgIdList);

    /**
     * 获取未签收的消息列表
     * @param acceptUserId
     * @return
     */
    List<com.yuxin.pojo.ChatMsg> getUnReadMsgList(String acceptUserId);
    
}



package com.yuxin.controller;

import com.yuxin.enums.OperatorFriendRequestTypeEnum;
import com.yuxin.enums.SearchFriendsStatusEnum;
import com.yuxin.enums.YuixnResultEnum;
import com.yuxin.pojo.ChatMsg;
import com.yuxin.pojo.Users;
import com.yuxin.pojo.bo.UsersBO;
import com.yuxin.pojo.vo.FriendRequestVO;
import com.yuxin.pojo.vo.MyFriendsVO;
import com.yuxin.pojo.vo.UsersVO;
import com.yuxin.service.UserService;
import com.yuxin.utils.FastDFSClient;
import com.yuxin.utils.FileUtils;
import com.yuxin.utils.IYuxinJSONResult;
import com.yuxin.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/u")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FastDFSClient fastDFSClient;

    @PostMapping("/registerOrLogin")
    public IYuxinJSONResult registerOrLogin(@RequestBody Users user) throws Exception {
        // 进行判断用户名和密码不能为空
        if (StringUtils.isBlank(user.getUsername())
                || StringUtils.isBlank(user.getPassword())) {
            return IYuxinJSONResult.build(YuixnResultEnum.USERS_NAME_AND_PWD_IS_NULL, null);
        }
        // 1.判断用户名是否存在，如果存在就登录，如果不存在则注册
        boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());
        Users userResult = null;
        if (usernameIsExist) {
            // 1.1 登录
            userResult = userService.queryUserForLogin(user.getUsername(), MD5Utils.getMD5Str(user.getPassword()));
            if (userResult == null) {
                return IYuxinJSONResult.build(YuixnResultEnum.USERS_NAME_AND_PWD_ERROR, null);
            }
        } else {
            // 1.2 注册
            user.setNickname(user.getUsername());
            user.setFaceImage("");
            user.setFaceImageBig("");
            // 设置用户密码
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            userResult = userService.saveUser(user);

        }
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userResult, usersVO);

        return IYuxinJSONResult.build(YuixnResultEnum.OK, usersVO);
    }


    @PostMapping("/uploadFaceBase64")
    public IYuxinJSONResult uploadFaceBase64(@RequestBody UsersBO usersBO) throws Exception {

        // 获取前端传过来的base64字符串，然后转换为文件对象再上传
        String base64Data = usersBO.getFaceData();
        String userFacePath = "d:\\img\\" + usersBO.getUserId() + "userface64.png";
        // 将用户传入的base64流 复制到本地备份并重命名和格式
        FileUtils.base64ToFile(userFacePath, base64Data);

        MultipartFile multipartFile = FileUtils.fileToMultipart(userFacePath);
        String faceFileUrl = fastDFSClient.uploadBase64(multipartFile);
        System.out.println("上传图片地址：" + faceFileUrl);

        // 获取缩略图的url
        String thump = "_80x80.";
        String[] split = faceFileUrl.split("\\.");
        String thumpImgUrl = split[0] + thump + split[1];

        // 更新用户头像
        Users user = new Users();
        user.setId(usersBO.getUserId());
        user.setFaceImage(thumpImgUrl);
        user.setFaceImageBig(faceFileUrl);

        Users users = userService.updateUserInfo(user);

        return IYuxinJSONResult.build(YuixnResultEnum.OK, users);
    }

    @PostMapping("/setNickName")
    public IYuxinJSONResult setNickName(@RequestBody UsersBO users) throws Exception {

        if (!StringUtils.isEmpty(users.getUserId()) && !StringUtils.isEmpty(users.getNickName())) {
            Users user = new Users();
            user.setId(users.getUserId());
            user.setNickname(users.getNickName());

            Users result = userService.updateUserInfo(user);

            return IYuxinJSONResult.build(YuixnResultEnum.OK, result);
        }

        return IYuxinJSONResult.build(YuixnResultEnum.USERS_NAME_IS_NULL, users);
    }

    @PostMapping("/searchFriends")
    public IYuxinJSONResult searchUser(@RequestParam("myUserId") String myUserId,
                                       @RequestParam("friendUsername") String friendUsername) throws Exception {

        // 判断不为空
        if (StringUtils.isBlank(myUserId)) {
            return IYuxinJSONResult.build(YuixnResultEnum.USERS_NAME_IS_NULL, null);
        } else if (StringUtils.isBlank(friendUsername)) {
            return IYuxinJSONResult.build(YuixnResultEnum.USERS_NAME_IS_NULL, null);
        }

        // 搜索用户不存在时
        // 搜索用户是自己时
        // 搜索用户已经是自己好友时

        Integer status = userService.preconditionSearchFriends(myUserId, friendUsername);

        if (status == SearchFriendsStatusEnum.OK.getStatus()) {
            String messageByKey = SearchFriendsStatusEnum.getMessageByKey(status);
            Users users = userService.queryUserInfoByUsername(friendUsername);
            UsersVO usersVO = new UsersVO();
            BeanUtils.copyProperties(users, usersVO);
            return IYuxinJSONResult.build(status, messageByKey, usersVO);
        } else {
            String messageByKey = SearchFriendsStatusEnum.getMessageByKey(status);
            return IYuxinJSONResult.build(status, messageByKey, null);
        }

    }

    @PostMapping("/addFriendRequest")
    public IYuxinJSONResult addFriendRequest(@RequestParam("myUserId") String myUserId,
                                             @RequestParam("friendUsername") String friendUsername) throws Exception {
        // 判断不为空
        if (StringUtils.isBlank(myUserId)) {
            return IYuxinJSONResult.build(YuixnResultEnum.USERS_NAME_IS_NULL, null);
        } else if (StringUtils.isBlank(friendUsername)) {
            return IYuxinJSONResult.build(YuixnResultEnum.USERS_NAME_IS_NULL, null);
        }

        Integer status = userService.preconditionSearchFriends(myUserId, friendUsername);
        if (status == SearchFriendsStatusEnum.OK.getStatus()) {
            SearchFriendsStatusEnum.getMessageByKey(status);
            userService.sendFriendRequest(myUserId, friendUsername);

        } else {
            String messageByKey = SearchFriendsStatusEnum.getMessageByKey(status);
            return IYuxinJSONResult.build(status, messageByKey, null);
        }

        return IYuxinJSONResult.build(YuixnResultEnum.OK, null);
    }

    /**
     * 查询用户请求
     * @param userId
     * @return
     */
    @PostMapping("/queryFriendRequests")
    public IYuxinJSONResult queryFriendRequests(@RequestParam("userId") String userId) {
    	
    	if (StringUtils.isBlank(userId)) {
    		return IYuxinJSONResult.build(YuixnResultEnum.IS_NULL, null);
    	}

        List<FriendRequestVO> data = userService.queryFriendRequestList(userId);
        return IYuxinJSONResult.build(YuixnResultEnum.OK, data);
    	
    }

    /**
     *  处理好友请求 通过或忽略
     * @param acceptUserId
     * @param sendUserId
     * @param operType
     * @return
     */
    @PostMapping("/operFriendRequests")
    public IYuxinJSONResult operFriendRequests(@RequestParam("acceptUserId") String acceptUserId,
                                               @RequestParam("sendUserId") String sendUserId,
                                               @RequestParam("operType") Integer operType) {

        if (StringUtils.isBlank(acceptUserId)
                || StringUtils.isBlank(sendUserId)
                || operType == null) {
            return IYuxinJSONResult.build(YuixnResultEnum.IS_NULL, null);
        }

        if (!OperatorFriendRequestTypeEnum.getType(operType)) {
            System.out.println("类型出错" + operType);
            return IYuxinJSONResult.build(YuixnResultEnum.VALUE_ERROR, null);
        }

        if (operType == OperatorFriendRequestTypeEnum.IGNORE.getType()) {
            // 如果是忽略，则直接删除好友请求的数据库表记录
            userService.deleteFriendRequest(sendUserId, acceptUserId);
        } else if (operType == OperatorFriendRequestTypeEnum.PASS.getType()) {
            // 如果是同意，则相互增加阿红有记录到数据库对应的表
            userService.passFriendRequest(sendUserId, acceptUserId);

        }
        // 当同意之后，查询用户好友列表并返回
        List<MyFriendsVO> myFriendsVOS = userService.queryMyFriends(acceptUserId);
        return IYuxinJSONResult.build(YuixnResultEnum.OK, myFriendsVOS);
    }

    /**
     * 查询用户的所有好友列表
     * @param myUserId
     * @return
     */
    @PostMapping("/myFriends")
    public IYuxinJSONResult myFriends(String myUserId) {

        if (StringUtils.isBlank(myUserId)) {
            return IYuxinJSONResult.build(YuixnResultEnum.IS_NULL, null);
        }
        // 1.查询好友列表
        List<MyFriendsVO> myFriendsVOS = userService.queryMyFriends(myUserId);

        return IYuxinJSONResult.build(YuixnResultEnum.OK, myFriendsVOS);
    }

    /**
     * 用户手机端获取未签收的消息列表
     * @param acceptUserId
     * @return
     */
    @PostMapping("/getUnReadMsgList")
    public IYuxinJSONResult getUnReadMsgList(@RequestParam("acceptUserId") String acceptUserId) {

        if (StringUtils.isBlank(acceptUserId)) {
            return IYuxinJSONResult.build(YuixnResultEnum.IS_NULL, null);
        }
        // 查询列表
        List<ChatMsg> unReadMsgList = userService.getUnReadMsgList(acceptUserId);
        System.out.println(unReadMsgList);
        return IYuxinJSONResult.build(YuixnResultEnum.OK, unReadMsgList);
    }


}

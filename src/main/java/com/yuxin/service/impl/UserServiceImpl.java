package com.yuxin.service.impl;

import com.alibaba.fastjson.JSON;
import com.yuxin.enums.MsgActionEnum;
import com.yuxin.enums.MsgSignFlagEnum;
import com.yuxin.enums.SearchFriendsStatusEnum;
import com.yuxin.mapper.*;
import com.yuxin.netty.ChatMsg;
import com.yuxin.netty.DataContent;
import com.yuxin.netty.UserChannelRel;
import com.yuxin.pojo.FriendsRequest;
import com.yuxin.pojo.MyFriends;
import com.yuxin.pojo.Users;
import com.yuxin.pojo.vo.FriendRequestVO;
import com.yuxin.pojo.vo.MyFriendsVO;
import com.yuxin.service.UserService;
import com.yuxin.utils.FastDFSClient;
import com.yuxin.utils.FileUtils;
import com.yuxin.utils.QRCodeUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UsersCumtomMapper usersCumtomMapper;
    
    @Autowired
    private FriendsRequestMapper friendsRequestMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private QRCodeUtils qrCodeUtils;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private ChatMsgMapper chatMsgMapper;

    @Autowired
    private MyFriendsMapper myFriendsMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {
        Users users = new Users();
        users.setUsername(username);
        Users result = usersMapper.selectOne(users);
        return result != null ? true : false;
    }

    /**
     * 设置 事务的级别为Propagation.SUPPORTS（支持当前事务，如果没有事务，则以非事务的方式运行）
     * @param username
     * @param password
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String password) {
        // 将对象封装为Example对象
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();

        criteria.andEqualTo("username", username);
        criteria.andEqualTo("password", password);

        Users result = usersMapper.selectOneByExample(userExample);
        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users saveUser(Users user) {

        String userId = sid.nextShort();
        // TODO 为用户生成一个唯一的二维码
        // 固定 格式 yuxin_qrcode:[username]
        String qrCodePath = "d:\\img\\user" + userId + "qrcode.png";
        qrCodeUtils.createQRCode(qrCodePath, "yuxin_qrcode:" + user.getUsername());
        MultipartFile qrCodeFile = FileUtils.fileToMultipart(qrCodePath);
        String qrUrl = "";
        try {
            qrUrl = fastDFSClient.uploadQRCode(qrCodeFile);
        } catch (IOException e) {
            return user;
        }
        user.setQrcode(qrUrl);
        user.setId(userId);
        usersMapper.insert(user);
        return user;
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users updateUserInfo(Users user) {
        int result = usersMapper.updateByPrimaryKeySelective(user);
        return queryUserById(user.getId());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Integer preconditionSearchFriends(String myUserId, String friendUsername) {
        // 搜索用户不存在时
        Users users = queryUserInfoByUsername(friendUsername);
        if (users == null) {
            return SearchFriendsStatusEnum.USER_NOT_EXIST.getStatus();
        }
        // 搜索用户是自己时
        if (myUserId.equals(users.getId())) {
            return SearchFriendsStatusEnum.NOT_YOURSELF.getStatus();
        }

        // 搜索用户已经是自己好友时s
        Example mfe = new Example(MyFriends.class);
        Example.Criteria mfc = mfe.createCriteria();
        mfc.andEqualTo("myUserId", myUserId);
        mfc.andEqualTo("myFriendUserId", users.getId());
        MyFriends myFriends = myFriendsMapper.selectOneByExample(mfe);

        if (myFriends != null) {
            return SearchFriendsStatusEnum.FRIENDS_ALLREADLY.getStatus();
        }
        return SearchFriendsStatusEnum.OK.getStatus();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfoByUsername(String username) {
        Example ue = new Example(Users.class);
        Example.Criteria criteria = ue.createCriteria();
        criteria.andEqualTo("username", username);
        Users users = usersMapper.selectOneByExample(ue);
        return  users;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void sendFriendRequest(String myUserId, String friendUsername) {
        // 根据用户名查询朋友信息
        Users friends = queryUserInfoByUsername(friendUsername);
        // 查询发送好友请求记录表
        Example fre = new Example(FriendsRequest.class);
        Example.Criteria criteria = fre.createCriteria();
        criteria.andEqualTo("sendUserId", myUserId);
        criteria.andEqualTo("acceptUserId", friends.getId());
        FriendsRequest friendsRequest = friendsRequestMapper.selectOneByExample(fre);
        // 如果为空表示不是好友
        if (friendsRequest == null) {
            String requestId = sid.nextShort();

            FriendsRequest request = new FriendsRequest();
            request.setId(requestId);
            request.setSendUserId(myUserId);
            request.setAcceptUserId(friends.getId());
            request.setRequestDateTime(new Date());
            friendsRequestMapper.insert(request);
        }

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    Users queryUserById(String userId) {
        Users users = usersMapper.selectByPrimaryKey(userId);
        return users;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public List<FriendRequestVO> queryFriendRequestList(String acceptUserId) {
		
		// TODO Auto-generated method stub
		return usersCumtomMapper.queryFriendRequestList(acceptUserId);
	}

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void deleteFriendRequest(String sendUserId, String acceptUserId) {
        Example fre = new Example(FriendsRequest.class);
        fre.createCriteria().andEqualTo("sendUserId", sendUserId)
                .andEqualTo("acceptUserId", acceptUserId);
        friendsRequestMapper.deleteByExample(fre);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void passFriendRequest(String sendUserId, String acceptUserId) {
        // 1.保存
        saveFriends(sendUserId, acceptUserId);
        saveFriends(acceptUserId, sendUserId);
        deleteFriendRequest(sendUserId, acceptUserId);

        // 使用websocket 主动推送消息到请求发起者，更新他的通讯录
        DataContent dataContent = new DataContent();
        dataContent.setAction(MsgActionEnum.PULL_FREIND.type);

        Channel sendChannel = UserChannelRel.get(sendUserId);
        if (sendChannel != null) {
            sendChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(dataContent)));
        }
    }

    /**
     *  保存用户
     * @param sendUserId
     * @param acceptUserId
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    void saveFriends(String sendUserId, String acceptUserId) {
        MyFriends myFriends = new MyFriends();
        myFriends.setId(Sid.next());
        myFriends.setMyUserId(acceptUserId);
        myFriends.setMyFriendUserId(sendUserId);
        myFriendsMapper.insert(myFriends);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<MyFriendsVO> queryMyFriends(String userId) {
        return usersCumtomMapper.queryMyFriends(userId);

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public String saveMsg(ChatMsg chatMsg) {
        com.yuxin.pojo.ChatMsg msgdb = new com.yuxin.pojo.ChatMsg();
        String id = sid.nextShort();
        msgdb.setId(id);
        msgdb.setAcceptUserId(chatMsg.getReceiverId());
        msgdb.setSendUserId(chatMsg.getSendUserId());
        msgdb.setCreateTime(new Date());
        msgdb.setMsg(chatMsg.getMsg());
        msgdb.setSignFlag(MsgSignFlagEnum.UNSIGN.getType());

        chatMsgMapper.insert(msgdb);

        return id;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void updateMsgSigned(List<String> msgIdList) {
        usersCumtomMapper.batchUpdateMsgSigned(msgIdList);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<com.yuxin.pojo.ChatMsg> getUnReadMsgList(String acceptUserId) {

        Example chatExample = new Example(com.yuxin.pojo.ChatMsg.class);
        Example.Criteria criteria = chatExample.createCriteria();
        criteria.andEqualTo("signFlag", 0);
        criteria.andEqualTo("acceptUserId", acceptUserId);
        List<com.yuxin.pojo.ChatMsg> chatMsgs = chatMsgMapper.selectByExample(chatExample);
        return chatMsgs;
    }
}

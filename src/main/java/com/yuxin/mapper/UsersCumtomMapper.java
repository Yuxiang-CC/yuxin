package com.yuxin.mapper;

import com.yuxin.pojo.Users;
import com.yuxin.pojo.vo.FriendRequestVO;
import com.yuxin.pojo.vo.MyFriendsVO;
import com.yuxin.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UsersCumtomMapper extends MyMapper<Users> {
	
	List<FriendRequestVO> queryFriendRequestList(String acceptUserId);

	List<MyFriendsVO> queryMyFriends(String userId);

	void batchUpdateMsgSigned(List<String> msgIdList);
}
package com.yuxin.mapper;

import com.yuxin.pojo.ChatMsg;
import com.yuxin.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMsgMapper extends MyMapper<ChatMsg> {
}
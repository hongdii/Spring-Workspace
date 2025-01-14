package com.kh.spring.chat.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.spring.chat.model.dao.ChatDAO;
import com.kh.spring.chat.model.vo.ChatMessage;
import com.kh.spring.chat.model.vo.ChatRoom;
import com.kh.spring.chat.model.vo.ChatRoomJoin;

@Service
public class ChatServiceImpl implements ChatService{
	
	@Autowired
	private ChatDAO dao;
	
	@Override
	public List<ChatRoom> selectChatRoomList(){
		return dao.selectChatRoomList();
	}
	
	@Override
	public int openChatRoom(ChatRoom chatRoom) {
		return dao.openChatRoom(chatRoom);
	}
	
	@Override
	public List<ChatMessage> joinChatRoom(ChatRoomJoin join){
		
		
		// 현재 회원이 해당 채팅방에 참여하고 있는지 확인
		int result = dao.joinCheck(join); // CHAT_ROOM_JOIN에 현재 유저+채팅방번호로 들어간 데이터가있는지 확인
		
		if(result == 0) { //미침여시 추가
			dao.joinChatRoom(join);
		}
		
		//채팅메세지 목록 조회
		return dao.selectChatMessage(join.getChatRoomNo());
	}
	
	
	@Override
	public int insertMessage(ChatMessage chatMessage) {
		
		
		return dao.insertMessage(chatMessage);
	}
	
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int exitChatRoom(ChatRoomJoin join) {
		
		// 채팅방 나가기
		int result = dao.exitChatRoom(join);
		// 채팅방 나가기 성공시
		if(result > 0) {
			// 현재방에 남아있는 인원을 확인하고,
			int cnt = dao.countChatRoomMember(join.getChatRoomNo());
			
			// 0명일경우 방을 닫기
			if(cnt == 0) {
				result = dao.closeChatRoom(join.getChatRoomNo());
			}
			
		}
		
		return result;
	}
	
	
	
	
	
	
	
	
	
	
	
}

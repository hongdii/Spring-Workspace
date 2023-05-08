package com.kh.spring.board.model.service;

import java.util.List;

import com.kh.spring.board.model.vo.Reply;

public interface ReplyService {
	
	// 댓글 등록
	int insertReply(Reply reply);
	// 댓글목록조회
	List<Reply> selectReplyList(int bno);
	// 댓글수정
	
	// 댓글 삭제
}

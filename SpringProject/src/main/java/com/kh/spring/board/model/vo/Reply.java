package com.kh.spring.board.model.vo;

import java.sql.Date;

import lombok.Data;

@Data
public class Reply {
	private int replyNo;
	private String replyContent;
	private int refBno;
	private String replyWriter;
	private int userNo; // 댓글 수정, 삭제를 위해 추가함
	private Date createDate;
	private String status;
}

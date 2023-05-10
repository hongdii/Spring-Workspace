package com.kh.spring.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.kh.spring.member.model.vo.Member;

@Component // 함수를 bean객체로 등록을 시켜줌
@Aspect // 공통 관심사를 모듈화 한 것
@Order(5)
public class AfterReturninTest {
	
	private Logger logger = LoggerFactory.getLogger(AfterReturninTest.class);
	
	// @AfterReturning : 메서드 실행 이후에 반환값을 얻어오는 기능의 어노테이션
	// returning : 반환값을 저장할 매개변수명을 지정하는 속성
	@AfterReturning(pointcut = "CommonPointcut.implPointcut()" , returning = "returnObj")
	//  returning="returnObj" 과 returnValue()의 Object returnObj은 동일해야한다.
	public void returnValue(JoinPoint jp, Object returnObj) { // 순서 바뀌면 안됨
		
		// returnObj 반환값이 Member 인가?
		if(returnObj instanceof Member) {
			Member loginMember = (Member)returnObj;
			loginMember.setNickName("오현오현지");
		}
		logger.info("return value : {} ", returnObj);
	}
	


}

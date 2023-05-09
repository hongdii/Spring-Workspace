package com.kh.spring.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AfterThrowingTest {
	
	// 발생한 예외를 얻어올수 있음 
	
	private Logger logger = LoggerFactory.getLogger(AfterThrowingTest.class);

	//@AfterThrowing : 메서드 실행 이후에 발생하는 예외를 얻어오는 어노테이션
	// 		throging : 반환한 예외값을 저장할 매개변수명 지정하는 속성
	@AfterThrowing(pointcut="CommonPointcut.implPointcut()", throwing="exceptionObj")
	public void serviceReturnValue(JoinPoint jp, Exception exceptionObj) {
		//JoinPoint는 사용하지 않아도 넣어줘야함 무조건 첫번째 자리
		StringBuilder sb = new StringBuilder("Exception : "+exceptionObj.getStackTrace()[0]); // 발생 예외중 첫번째값만 얻어오기
		sb.append("\n에러 메세지 : "+exceptionObj.getMessage());
		logger.error(sb.toString());
	}
}

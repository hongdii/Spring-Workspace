package com.kh.spring.common.aop;

import org.aspectj.lang.annotation.Pointcut;

public class CommonPointcut {

	// 게시판 서비스용 PointCut
	@Pointcut("execution(* com.kh.spring.board..*Impl*.*(..))")
	public void boardPointcut() {}

	// 모든 서비스용 Pointcut
					// 모든 경로 아래에 모든 Impl 아래에 모든 메서드
	@Pointcut("execution(* com.kh.spring.board..*Impl*.*(..))")
	public void implPointcut() {}
}

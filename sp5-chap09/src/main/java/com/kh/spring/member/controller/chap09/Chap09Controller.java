package com.kh.spring.member.controller.chap09;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hello?name=bk")
public class Chap09Controller {

	public String helloMember() {
		return "index";
	}
	

}

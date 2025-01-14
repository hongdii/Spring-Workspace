package com.kh.spring.board.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.member.model.vo.Member;

@Controller
@RequestMapping("/board")
public class BoardController {

	@Autowired
	private BoardService boardService;
	
	private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

	/*
	 * 게시글 목록 조회 서비스
	 * 
	 * @PathVariable : URL경로에 포함되어있는 값을 변수로 사용할수 있게 하는 역할 => ★자동으로 request scope에 등록
	 * 된다. ==> jsp에서 ${value} el로 작성가능하다.
	 * 
	 * PathVariable : 요청 자원을 식별하는 경우에 사용
	 * 
	 * QueryString : 정렬, 검색등 필터링 옵션이 있을경우 사용
	 */
	@GetMapping("/list/{boardCode}")
	public String boardList(@PathVariable("boardCode") String boardCode,
			@RequestParam(value = "cpage", defaultValue = "1") int currentPage, Model model,
			@RequestParam Map<String, Object> paramMap) {
		// 검색요청이 있었다면, paramMap안에는 keyword, condition이 들어가 있을것.
		Map<String, Object> map = new HashMap();
		// 게시글 목록 조회 서비스 호출시 작업 내용
		// 1) 게시판 이름 조회
		
		// 검색요청을하지 않은경우
		if (paramMap.get("condition") == null) {
			boardService.selectBoardList(currentPage, boardCode, map);
		} else {
			// 검색요청을 한경우
			// 검색조건을 추가한 상태로 게시글 셀렉트?
			paramMap.put("currentPage", currentPage);
			paramMap.put("boardCode",boardCode);
			boardService.selectBoardList(paramMap, map);

		}

		// model.addAttribute("list",list);
		// model.addAttribute("pi",pi);
		model.addAttribute("map", map);

		return "board/boardListView";
	}

	// 게시글 상세조회
	@GetMapping("/detail/{boardCode}/{boardNo}")
	public String boardDetail(@PathVariable("boardCode") String boardCode, @PathVariable("boardNo") int boardNo,
			@RequestParam(value = "cpage", required = false, defaultValue = "1") int cp, Model model,
			HttpSession session, HttpServletRequest req, HttpServletResponse resp) {

		// 게시글 상세조회 서비스 호출
		Board detail = boardService.selectBoardDetail(boardNo);

		// 조회수 중복 증가 방지 코드(본인글은 조회수 증가 안되게) -> 쿠키를 활용할 예정

		if (detail != null) { // 상세조회 성공

			Member loginUser = (Member) session.getAttribute("loginUser");

			int memberNo = 0;

			if (loginUser != null) { // 로그인한 상태일때
				memberNo = loginUser.getUserNo();
			}

			// 글쓴이와 현재 상세보기요청을한 클라이언트가 같지 않을 경우에만 조회수 증가서비스 호출.
			if (Integer.parseInt(detail.getBoardWriter()) != memberNo) {
				Cookie cookie = null;

				Cookie[] cArr = req.getCookies(); // 쿠키얻어보기

				if (cArr != null && cArr.length > 0) { // 얻어온 쿠키가 있을경우

					for (Cookie c : cArr) {

						if (c.getName().equals("readBoardNo")) {
							cookie = c;
						}
					}

				}
				int result = 0;
				if (cookie == null) { // 기존에 readBoardNo라는 이름의 쿠키가 없던 경우
					cookie = new Cookie("readBoardNo", boardNo + "");
					result = boardService.updateReadCount(boardNo); /// 조회수 증가서비스 호출
				} else {
					// readBoardNo가 이미 존재할경우 -> 쿠키에 저장된값 뒤쪽에 현재 조회된 게시글 번호를 추가
					// 단, 기존 쿠키값중에 중복되는 번호가 없어야한다.
					String temp[] = cookie.getValue().split("/");// 기존 value
					// "readBoardNo" / "1/2/3/4/5/6/7/8/9/100"

					List<String> list = Arrays.asList(temp); // 배열 --> List로 변환시켜주는 함수

					// List.indexOf(Object) :
					// - List에서 Object와 일치하는 부분의 인덱스를 반환
					// - 일치하는부분이 없으면 -1을 반환
					if (list.indexOf(boardNo + "") == -1) {// 즉 , 기존값에 같은 글번호가 없다면
						cookie.setValue(cookie.getValue() + "/" + boardNo);
						result = boardService.updateReadCount(boardNo); // 조회수 증가서비스 호출
					}

				}
				
				if (result > 0) {

					cookie.setPath(req.getContextPath());
					cookie.setMaxAge(60 * 60 * 1); // 1시간
					resp.addCookie(cookie);
				}

			}

		}
		
		model.addAttribute("b", detail);
		return "board/boardDetailView";
	}
	
	
	@GetMapping("/enrollForm/{boardCode}")
	public String boardEnrollForm(
				@PathVariable("boardCode") String boardCode,
				Model model,
				@RequestParam(value="mode", defaultValue="insert", required = false) String mode,
				@RequestParam(value="bno", defaultValue="0", required = false) int bno
			) {
		if(mode.equals("update")) {
			//수정하기 페이지 요청
			// 선택한 게시판 정보 조회후 이동.
			Board b = boardService.selectBoardDetail(bno);
			
			model.addAttribute("b",b);
		}
		
		if(boardCode.equals("N")) {
			return "board/boardEnrollForm";
		}else {
			return "board/boardPictureEnrollForm";
		}
		
	}
	
	@PostMapping("/insert/{boardCode}")
	public String insertBoard(
			Board b ,
			@RequestParam(value="mode" , required = false, defaultValue = "insert") String mode,
			@RequestParam(value = "images" , required = false) List<MultipartFile> imgList, //업로드용이미지파일
			@RequestParam(value = "upfile" , required = false) MultipartFile upfile, // 첨부파일
			@PathVariable("boardCode") String boardCode,
			HttpSession session,
			Model model,
			@RequestParam(value="deleteList", required=false) String deleteList
			) {
		// 사진게시판들 이미지를 저장할 저장경로 얻어오기
		String webPath = "/resources/images/boardT/";
		String serverFolderPath = session.getServletContext().getRealPath(webPath);
		b.setBoardCd(boardCode);
		
		logger.info("insert함수 실행");
		
		int result = 0;
		
		if(mode.equals("insert")) {
			
			// db에 Board테이블에 데이터 추가
			try {
				result = boardService.insertBoard(b , imgList, webPath, serverFolderPath);
			}catch(Exception e){
				logger.error("insert메서드 에러");				
			}
		
		} else {
			// 게시글 수정 서비스 호출
			// b객체안에 boardNo이 들어간상태일것.
			try {
				result = boardService.updateBoard(b,imgList,webPath,serverFolderPath,deleteList);
			} catch (Exception e) {
				logger.error("update메서드 에러");		
			}
		}
		
		// 첨부파일 업로드 -> Board 테이블안에 ORGIN_NAME, CHAGNE_NAME
		
		
		
		if(result > 0) { // 성공적으로 추가시
			session.setAttribute("alertMsg", "게시글 작성에 성공하셨습니다.");
			return "redirect:../list/"+boardCode;
		}else {// errorPage 포워딩
			model.addAttribute("errorMsg","게시글 작성 실패");
			return "common/errorPage";
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}

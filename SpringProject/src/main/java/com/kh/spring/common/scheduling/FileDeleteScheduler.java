package com.kh.spring.common.scheduling;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.vo.BoardImg;

@Component
public class FileDeleteScheduler {

	private Logger logger = LoggerFactory.getLogger(FileDeleteScheduler.class);

	@Autowired
	private ServletContext application;

	@Autowired
	private BoardService service;

	// 1. BOARD_IMG 테이블안에 있는 이미지 목록들 모두 조회하여
	// 2. images/boardT 디렉토리안에 있는 이미지들과 대조하여
	// 3. 일치하지 않는 이미지 파일들을 삭제(db에는 없는 데이터인데, boardT안에는 존재하는 경우)
	// 4. 우선 5초 간격으로 테스트후, 정상적으로 작동한다면 매달 1일 정시에 실행되는 스케줄러로 만들기

	@Scheduled(fixedDelay = 5000)
	/* (cron = "0 0 0 1 * *") */
	public void deleteFile() {
		logger.info("파일 삭제 시작");

		// 1) board_img 테이블 안에 있는 모든 파일목록들 조회
		ArrayList<String> biList = service.selectAllFileList();

		// 2) images/board 폴더 아래에 존재하는 모든 이미지 파일목록 조회(File클래스 활용)
		File path = new File(application.getRealPath("/resources/images/boardT"));
		// path가 참조하고 있는 폴더에 들어가서 모든 파일을 File배열로 얻어오기
		File[] deleteList = path.listFiles();
		List<File> fileList = Arrays.asList(deleteList);
		
		logger.info(biList.toString());
		logger.info(fileList.toString());
		// 3) 두 목록을 비교해서 일치하지 않는 파일 삭제(삭제시 File클래스의 delete()활용)
		int result =0;
		
		if(!biList.isEmpty()) {
			for(int i=0 ; i < biList.size() ; i++) { 
				boolean isTrue = false;
				
				for(int j=0; j < fileList.size() ; j++) {
					
					if(biList.get(i).equals(fileList.get(j).getName())) {
						isTrue = true;
					}
					
				}
				if(isTrue == false) {
					fileList.get(i).delete();
				}
				
			}
			
		}
		
		// 방법2 
		// List.indexOf(value) : List에 value와 같은 값이 있으면 인덱스를 반환/ 없으면 -1을 반환
		
		
		
			logger.info("xxx파일 삭제함");

			logger.info("서버 파일 삭제작업 끝");
		

		
	}

}

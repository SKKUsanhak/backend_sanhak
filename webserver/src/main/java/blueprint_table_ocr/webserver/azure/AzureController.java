package blueprint_table_ocr.webserver.azure;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AzureController {
	
	/*	앞으로 해야 할 것 + 질문
	 * 	1. 로그인 / 보안 기능
	 * 		1-1. Azure Credential 기능이 있고 Spring Security가 있는데 어떻게 사용해야 할 지 모르겠음.
	 * 		1-2. 아마도 에너지 평가사를 대상으로 하는 웹페이지가 될 텐데 여러 명이 사용할 때 서버를 다르게 구성할 필요가 있는지 (/user_id?= ...) 이렇게?
	 * 	2. 리액트와 연결
	 * 		2-1. 리액트로 .csv 또는 documentTable 자료형을 전달해야 할 것 같은데 리액트에서 이러한 자료형을 받을 수 있는지 -> .json으로 변환해서 넘겨야 하는지
	 * 	3. 사용자 검수 기능
	 * 		3-1. 사용자가 범위 지정하는 행 & 열 병합 기능 / 열 추가&삭제 기능 / 행 추가&삭제 기능 만들어야 함. -> 이후 리액트와 연결
	 * 		3-2. 행 내에서 일괄적으로 . / , 지정 기능? (미정)
	 * 		3-3. 테이블 이름 지정, 데이터베이스 이름 지정 기능
	 * 		3-4. 행 & 열 병합 시 계속 데이터를 가지고 있어야 하는데 백엔드에서 가지고 있어야 하는지(실시간 수정)
	 * 			 아니면 프론트엔드에서 수정 후 업로드(수정 완료 후 한번만 수정) 인지
	 * 	4. 서버 페이지
	 * 		4-1. 로그인 페이지, 입력 페이지, ... 여러 개의 페이지 구성해야 함
	 * */

	private AzureService service;
	   
	   public AzureController(AzureService service) {
	      this.service = service;
	   }
	   
	   @PostMapping("/upload") //파일 다운받기 -> Talend API 사용해서 정상 작동 확인함. -> 로컬에 csv 저장이 아니라 리턴하는 방식 생각해 봐야 함.
	   public String UploadFile(@RequestParam("file") MultipartFile file) {
	      if (!file.isEmpty()) {
	            try {
	               service.analyzeTable(file);
	               return "success";
	            } catch (Exception e) {
	                return "Failed to upload file: " + e.getMessage();
	            }
	        } else {
	            return "Failed to upload file because it was empty.";
	        }
	   }
}

package blueprint_table_ocr.webserver.azure;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.azure.ai.formrecognizer.documentanalysis.models.DocumentTable;

@RestController
public class AzureController {
	
	/*	앞으로 해야 할 것 + 질문
	 * 	1. 로그인 / 보안 기능 (후순위)
	 * 		1-1. Azure Credential 기능이 있고 Spring Security가 있는데 어떻게 사용해야 할 지 모르겠음.
	 * 		1-2. 아마도 에너지 평가사를 대상으로 하는 웹페이지가 될 텐데 여러 명이 사용할 때 서버를 다르게 구성할 필요가 있는지 (/user_id?= ...) 이렇게?
	 * 	2. 리액트와 연결하는 방법
	 * 		2-1. 리액트로 DocumentTable 형태로 리턴 시 json 응답이 리턴됨 -> 리액트에서 바로 사용가능한지 아니면 정리를 해서 사용해야 하는지 아직 모름
	 * 	3. 사용자 검수 기능
	 * 		3-1. 사용자가 범위 지정하는 행 & 열 병합 기능 / 열 추가&삭제 기능 / 행 추가&삭제 기능 만들어야 함. -> 프론트엔드 구현?
	 * 		3-2. 행 내에서 일괄적으로 . / , 지정 기능? (미정)
	 * 		3-3. 테이블 이름 지정, 데이터베이스 이름 지정 기능
	 * 		3-4. 행 & 열 병합 시 계속 데이터를 가지고 있어야 하는데 백엔드에서 가지고 있어야 하는지(실시간 수정)
	 * 			 아니면 프론트엔드에서 수정 후 업로드(수정 완료 후 한번만 수정) 인지
	 * 		3-5. 개별 셀 수정 기능
	 * 	4. 서버 페이지 (후순위)
	 * 		4-1. 로그인 페이지, 입력 페이지, ... 여러 개의 페이지 구성해야 함
	 * */

	private AzureService service;
	   
	   public AzureController(AzureService service) {
	      this.service = service;
	   }
	   
	   @PostMapping("/upload") // 사용자가 POST한 파일 다운받기 -> Talend API 사용해서 정상 작동 확인함. -> table을 리액트로 리턴
	   public List<DocumentTable> UploadFile(@RequestParam("file") MultipartFile file) throws IOException {
		   return service.analyzeTable(file);
	   }
	   
	   @GetMapping("/upload") // 사용자가 업로드 하러 들어갈 페이지
	   public String UploadPage() {
		   return "";
	   }
}

package blueprint_table_ocr.webserver.azure;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.POIDocument;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AzureController {
	
	/*	앞으로 해야 할 것 + 질문
	 * 	1. 로그인 / 보안 기능 (후순위)
	 * 		1-1. Azure Credential 기능이 있고 Spring Security가 있는데 어떻게 사용해야 할 지 모르겠음.
	 * 		1-2. 아마도 에너지 평가사를 대상으로 하는 웹페이지가 될 텐데 여러 명이 사용할 때 서버를 다르게 구성할 필요가 있는지 (/user_id?= ...) 이렇게?
	 * 	2. 리액트와 연결하는 방법 (연결 완료)
	 * 		2-1. 리액트로 DocumentTable 형태로 리턴 시 json 응답이 리턴됨 -> parse 기능 이용해 구현할 예정
	 * 	3. 사용자 검수 기능
	 * 		3-1. 사용자가 범위 지정하는 행 & 열 병합 기능 / 열 추가&삭제 기능 / 행 추가&삭제 기능 만들어야 함. -> 프론트엔드 구현?
	 * 		3-2. 행 내에서 일괄적으로 . / , 지정 기능? (미정)
	 * 		3-3. 테이블 이름 지정, 데이터베이스 이름 지정 기능
	 * 		3-4. 행 & 열 병합 시 계속 데이터를 가지고 있어야 하는데 백엔드에서 가지고 있어야 하는지(실시간 수정)
	 * 			 아니면 프론트엔드에서 수정 후 업로드(수정 완료 후 한번만 수정) 인지
	 * 		3-5. 개별 셀 데이터 수정 기능
	 * 	4. 서버 페이지 (후순위)
	 * 		4-1. 로그인 페이지, 입력 페이지, ... 여러 개의 페이지 구성해야 함
	 * */

	private AzureService service;
	   
	   public AzureController(AzureService service) {
	      this.service = service;
	   }
	   
	   @PostMapping("/upload") // 사용자가 POST한 파일 다운받기 -> Talend API 사용해서 정상 작동 확인함. -> table을 리액트로 리턴
	   public ResponseEntity<byte[]> UploadFile(@RequestParam("file") MultipartFile file) throws IOException {
		   XSSFWorkbook workbook = service.analyzeTable(file);
		   
		   ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			workbook.write(byteArrayOutputStream);
			workbook.close();

			byte[] bytes = byteArrayOutputStream.toByteArray();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDispositionFormData("attachment", "documentTables.xlsx");
			headers.setContentLength(bytes.length);

			return ResponseEntity.ok().headers(headers).body(bytes);
	   }
	   
	   @GetMapping("/upload") // 사용자가 업로드 하러 들어갈 페이지
	   public String UploadPage() {
		   return "Upload Page";
	   }
	   
	   @GetMapping("/hello") // 연결 테스트
	   public String HelloPage() {
		   return "Hello World";
	   }
	   
	   @PostMapping("/final-result")
	   public ResponseEntity<String> XLSXToDB(@RequestParam("file") MultipartFile file, @RequestParam("ColumnRanges") String columnRanges) throws IOException {
		   service.uploadToDB(file, columnRanges);
		   // columnRanges 처리
		   System.out.println("Column Ranges: " + columnRanges);
		   
		   // 성공 응답 반환
		   return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully");
	   }
	   
//	   @PostMapping("/test")
//	   public void test(@RequestParam("file") MultipartFile file) throws IOException {
//		   XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
//	       XSSFSheet sheet = workbook.getSheetAt(0); 
//	       service.compressColumn(sheet,3);
//	       try (FileOutputStream fileOut = new FileOutputStream("test3row_compressed.xlsx")) {
//               workbook.write(fileOut);
//           }
//       	   catch (IOException e) {
//       			e.printStackTrace();
//       	   }
//	   }
}

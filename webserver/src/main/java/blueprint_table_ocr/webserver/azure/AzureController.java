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
	   
}

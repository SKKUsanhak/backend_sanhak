package blueprint_table_ocr.webserver.azure.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import blueprint_table_ocr.webserver.azure.service.AzureService;

@RestController
public class AzureController {
	
	private AzureService service;

	   
	public AzureController(AzureService service) {
		this.service = service;
	}
	   
	@PostMapping("/upload") // 사용자가 POST한 파일 다운받기 -> Talend API 사용해서 정상 작동 확인함. -> table을 리액트로 리턴//이거는 최초 데이터 분석
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
/*
	@PostMapping("/files/{fileId}/finalTables/{tableId}")//table id를 주면 해당 테이블만 final data로 옮기고 temp에서는 삭제 *POST/{{baseUrl}}/files/:fileId/finalTables/:tableId
	public void saveFinalTable(@PathVariable long fileId,@PathVariable long tableId) {
		excelService.saveToFinalTable(tableId);
	}
	 */
}

package blueprint_table_ocr.webserver.azure.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import blueprint_table_ocr.webserver.azure.service.AzureService;
import blueprint_table_ocr.webserver.azure.service.ExcelService;
import blueprint_table_ocr.webserver.datapart.Data;
import blueprint_table_ocr.webserver.datapart.OwnerFile;
import blueprint_table_ocr.webserver.datapart.TableDoc;

@RestController
public class AzureController {

	   private AzureService service;
	   private ExcelService excelService;
	   
	   public AzureController(AzureService service, ExcelService excelservice) {
	      this.service = service;
	      this.excelService = excelservice;
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
	   
	   
	 
	   @PostMapping("/files")//db에 excel파일을 저장 *POST/{{baseUrl}}/files
	    public String SaveExcelDatabase(@RequestPart("file") MultipartFile file, @RequestParam("fileName") String fileName) {
	        try {
	            excelService.saveTempDb(file,fileName);
	            return "File uploaded and data saved to database successfully.";
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "Failed to upload file and save data to database.";
	        }
	    }
		 
	   //delete
	   @DeleteMapping("/files/{fileId}")//파일 삭제하기 *DELETE/ {{baseUrl}}/files/:fileId
		public void DeleteFromDatabase (@PathVariable long fileId) {
			excelService.deleteFile(fileId);
		}
	   
	   @DeleteMapping("/files/{fileId}/tables/{tableId}")//테이블 삭제하기 *DELETE/ {{baseUrl}}/files/:fileId/tables/:tableId
		public void DeletetableFromDatabase (@PathVariable long fileId, @PathVariable long tableId) {
			excelService.deleteTable(tableId);
		}
	   
	   @DeleteMapping("/files/{fileId}/tables/{tableId}/rows/{rowIndex}")//행 삭제하고 아래있는거 땡기기 *DELETE/ {{baseUrl}}/files/:fileId/tables/:tableId/rows/:rowIndex
	   public void DeleteRow(@PathVariable long fileId,@PathVariable long tableId,@PathVariable int rowIndex) {
		   excelService.deleteRow(tableId,rowIndex);
	   }
	   
	   @DeleteMapping("/files/{fileId}/tables/{tableId}/columns/{columnIndex}")//열 삭제하고 아래있는거 땡기기 *DELETE/ {{baseUrl}}/files/:fileId/tables/:tableId/columns/:columnIndex
	   public void DeleteColumn(@PathVariable long fileId,@PathVariable long tableId,@PathVariable int columnIndex) {
		   excelService.deleteColumn(tableId,columnIndex);
	   }
	   
	   
	   //read
		@GetMapping("/files")//파일 리스트 보여주기 *GET/ {{baseUrl}}/files
		public List<OwnerFile> listAllfiles() {
			List<OwnerFile> filelists = excelService.findAllFile();
			return filelists;
		}
		
	   @GetMapping("/files/{fileId}/tables")//해당 아이디를 가진 파일의 테이블 리스트 보여주기 *GET/ {{baseUrl}}/files/:fileId/tables
		public List<TableDoc> listAlltables(@PathVariable long fileId) {
			List<TableDoc> tablelists = excelService.findTableById(fileId);
			return tablelists;
	   }
	   
	   @GetMapping("/files/{fileId}/tables/{tableId}/datas")//해당 아이디를 가진 테이블의데이터  보여주기  *GET/ {{baseUrl}}/files/:fileId/tables/:tableId/datas
		public List<? extends Data> showtables(@PathVariable long fileId,@PathVariable long tableId) {
		   	if(excelService.isFinalTable(tableId)==true) {
		   		List<? extends Data> datalists = excelService.findDataById(tableId);
		   		return  datalists;	
		   	}
		   	else {
		   		List<? extends Data> tempdatalists = excelService.findTempDataById(tableId);
				return  tempdatalists;	
		   	}
			 
	   }
	   
	   //update
	   @PatchMapping("/files/{fileId}/tables/{tableId}/columns/{columnIndex}/rows/{rowIndex}")//원하는 셀 업데이트 *PATCH/{{baseUrl}}/files/:fileId/tables/:tableId/columns/:columnIndex/rows/:rowIndex
	   public void updateCell (@PathVariable long fileId,@PathVariable long tableId,@PathVariable int columnIndex,@PathVariable int rowIndex,@RequestBody Map<String,String> Content) { 
		   String contents = Content.get("contents");
		   excelService.updateCell(tableId,columnIndex,rowIndex,contents);
	   }
	   
	   @PatchMapping("/files/{fileId}")//원하는 파일 이름 업데이트 *PATCH/{{baseUrl}}/files/:fileId
	   public void updateFileName (@PathVariable long fileId ,@RequestBody Map<String,String> Content) { 
		   String contents = Content.get("contents");
		   excelService.updateFileName(fileId, contents);
	   }
	   
	   @PatchMapping("/files/{fileId}/tables/{tableId}")//원하는 테이블 이름 업데이트 *PATCH/ {{baseUrl}}/files/:fileId/tables/:tableId
	   public void updateTableName (@PathVariable long fileId,@PathVariable long tableId ,@RequestBody Map<String,String> Content) { 
		   String contents = Content.get("contents");
		   excelService.updateTableName(tableId, contents);
	   }
	   
	   @PatchMapping("/files/{fileId}/tables/{tableId}/columns/{columnIndex}")//열의 이름 업데이트 *PATCH/ {{baseUrl}}/files/:fileId/tables/:tableId/columns/:columnIndex
	   public void updateColumnName (@PathVariable long fileId,@PathVariable long tableId,@PathVariable int columnIndex,@RequestBody Map<String,String> Content) {
		   String contents = Content.get("contents");
		   excelService.updateColumnName(tableId, columnIndex,contents);
	   }
	   @PatchMapping("/files/{fileId}/updateDate")//*PATCH/ {{baseUrl}}/files/:fileId/updateDate
	   public void updateDate(@PathVariable long fileId){
		   excelService.updateDate(fileId);
	   }
	   
	   ////create
	   @PostMapping("/files/{fileId}/tables")//테이블 새로 만들기 *POST/{{baseUrl}}/files/:fileId/tables
	   public void createTable (@PathVariable long fileId, @RequestBody Map<String,String> Content) {
		   String tablename = Content.get("contents");
		   excelService.createNewTable(fileId, tablename);
	   }
	   
	   @PostMapping("/files/{fileId}/tables/{tableId}/columns/{columnIndex}")//열 새로 만들기 *POST/{{baseUrl}}/files/:fileId/tables/:tableId/columns/:columnIndex
	   public String createcolumn(@PathVariable long fileId,@PathVariable long tableId,@PathVariable int columnIndex,@RequestBody Map<String,String> Content) {
		   String contents = Content.get("contents");
		   return excelService.createNewColumn(tableId,columnIndex,contents);
	   }
	   
	   @PostMapping("/files/{fileId}/tables/{tableId}/rows/{rowIndex}")//행 새로 만들기 *POST/ {{baseUrl}}/files/:fileId/tables/:tableId/rows/:rowIndex
	   public String createrow(@PathVariable long fileId,@PathVariable long tableId,@PathVariable int rowIndex) {
		   return excelService.createNewRow(tableId,rowIndex);
	   }
	   
	   /////////////////////////////////////////////////////////////////////////////////////////final data part
	   

	   
	   @PostMapping("/files/{fileId}/finalTables/{tableId}")//table id를 주면 해당 테이블만 final data로 옮기고 temp에서는 삭제 *POST/{{baseUrl}}/files/:fileId/finalTables/:tableId
	   public void saveFinalTable(@PathVariable long fileId,@PathVariable long tableId) {
		   excelService.saveToFinalTable(tableId);
	   }
	 
}

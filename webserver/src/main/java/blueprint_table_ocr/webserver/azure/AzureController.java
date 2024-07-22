package blueprint_table_ocr.webserver.azure;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
	   
	   @GetMapping("/upload") // 사용자가 업로드 하러 들어갈 페이지
	   public String UploadPage() {
		   return "Upload Page";
	   }
	   
	 
	   @PostMapping("/final-result")//db에 excel파일을 저장
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
	   @DeleteMapping("/delete-file")//파일 삭제하기
		public void DeleteFromDatabase (@RequestParam long id) {
			excelService.deleteFile(id);
		}
	   
	   //read
		@GetMapping("/show-file")//파일 리스트 보여주기
		public List<OwnerFile> listAllfiles() {
			List<OwnerFile> filelists = excelService.findAllFile();
			return filelists;
		}
		
	   @GetMapping("/show-table")//해당 아이디를 가진 파일의 테이블 리스트 보여주기
		public List<TableDoc> listAlltables(@RequestParam long id) {
			List<TableDoc> tablelists = excelService.findTableById(id);
			return tablelists;
	   }
	   
	   @GetMapping("/show-temp-data")//해당 아이디를 가진 테이블의 템프 데이터  보여주기
		public List<TempTableData> lilltables(@RequestParam long tableId) {
			List<TempTableData> tempdatalists = excelService.findTempDataById(tableId);
			return  tempdatalists;
	   }
	   
	   //update
	   @PatchMapping("/update-cell")//원하는 셀 업데이트(tempdata용)
	   public void updateCell (@RequestParam long cellid ,@RequestBody Map<String,String> Content) { 
		   String contents = Content.get("contents");
		   excelService.updateTempCell(cellid, contents);
	   }
	   
	   @PatchMapping("/update-table-name")//원하는 테이블 이름 업데이트
	   public void updateTableName (@RequestParam long tableid ,@RequestBody Map<String,String> Content) { 
		   String contents = Content.get("contents");
		   excelService.updateTableName(tableid, contents);
	   }
	   
	   @PatchMapping("/update-column-name")//열의 이름 업데이트
	   public void updateColumnName (@RequestParam long tableid, @RequestParam int columnnumber,@RequestBody Map<String,String> Content) {
		   String contents = Content.get("contents");
		   excelService.updateColumnName(tableid, columnnumber,contents);
	   }
	   
	   ////create
	   @PostMapping("/create-new-table")//테이블 새로 만들기
	   public void createTable (@RequestParam long fileid, @RequestBody Map<String,String> Content) {
		   String tablename = Content.get("contents");
		   excelService.createNewTable(fileid, tablename);
	   }
	   
	   //새로운 셀 만들기
	   public static class CreateRequest {
	        public long tableId;
	        public int row;
	        public int column;
	        public String contents;
	    }
	   
	   @PostMapping("/create-new-cell")
	   public String createTable(@RequestBody CreateRequest createinfo) {
		   return excelService.createNewCell(createinfo); 
	   }
	   
	   @PostMapping("/create-new-column")//열 새로 만들기
	   public String createcolumn(@RequestParam("tableid") long tableid,@RequestParam("colindex") int colindex,@RequestBody Map<String,String> Content) {
		   String contents = Content.get("contents");
		   return excelService.createNewColumn(tableid,colindex,contents);
		   
		   
	   }
	   
	   @GetMapping("/save-final-data")//그냥 지금 있는거 그대로 final로 옮기기
	   public void saveFinal() {
		   excelService.saveFinalDb();
	   }
	   
	   @PatchMapping("/update-final-data") //final data 업데이트 하기
	   public String updateFinal(@RequestParam long cellid ,@RequestBody Map<String,String> Content) {
		   if(excelService.isFinalTableEmpty()==true) {
			   excelService.saveFinalDb();
		   }
		   String contents = Content.get("contents");
		   excelService.updateFinalCell(cellid, contents);
		
		   return "update complete";

	   }
}

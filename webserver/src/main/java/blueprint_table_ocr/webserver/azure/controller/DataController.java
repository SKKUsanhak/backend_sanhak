package blueprint_table_ocr.webserver.azure.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import blueprint_table_ocr.webserver.azure.Dto.NameDto;
import blueprint_table_ocr.webserver.azure.Dto.VersioningDto;
import blueprint_table_ocr.webserver.azure.service.DataService;
import blueprint_table_ocr.webserver.azure.service.DataVersionService;
import blueprint_table_ocr.webserver.datapart.DataVersionControl;
import blueprint_table_ocr.webserver.datapart.TempTableData;
import jakarta.validation.Valid;

@RestController
public class DataController {
	private DataService dataService;
	private DataVersionService dataVersionService;
	public DataController(DataService dataService,DataVersionService dataVersionService) {
		this.dataService = dataService;
		this.dataVersionService = dataVersionService;
	}
	//create new version
	@PostMapping("buildings/{buildingId}/files/{fileId}/tables/{tableId}/versions")
	public ResponseEntity<DataVersionControl> createVersion(@PathVariable long buildingId,@PathVariable long fileId,@PathVariable long tableId,
			@RequestBody @Valid VersioningDto versioningDto){
		DataVersionControl newVersion = dataVersionService.createNewVersion(tableId,versioningDto);//새로운 version만들기, 그 version참조하는 복붙데이터 추가
		return ResponseEntity.status(HttpStatus.CREATED).body(newVersion); 
	}
	   
	//create
	//열 새로 만들기
	@PostMapping("buildings/{buildingId}/files/{fileId}/tables/{tableId}/columns")  
	public String createColumn(@PathVariable long buildingId, @PathVariable long fileId,@ PathVariable long tableId,
			@RequestParam int columnIndex, @RequestBody @Valid NameDto nameDto) {
		return dataService.createNewColumn(tableId,columnIndex,nameDto);
	}
	//행 새로 만들기 
	@PostMapping("buildings/{buildingId}/files/{fileId}/tables/{tableId}/rows") 
	public String createRow(@PathVariable long buildingId,@PathVariable long fileId,@PathVariable long tableId,@RequestParam int rowIndex) {
		return dataService.createNewRow(tableId,rowIndex);
	}
	
	//read
	//해당 아이디를 가진 version의 데이터 
	@GetMapping("buildings/{buildingId}/files/{fileId}/tables/{tableId}/datas")
	public ResponseEntity<List<TempTableData>> showData(@PathVariable long buildingId,@PathVariable long fileId,@PathVariable long tableId
			,@RequestParam long versionId){
		List<TempTableData> dataList = dataService.getDatas(versionId);
		if (dataList.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(dataList);
	}
	   
	//update   
	@PatchMapping("buildings/{buildingId}/files/{fileId}/tables/{tableId}/datas")//원하는 셀 업데이트 
	public ResponseEntity<TempTableData> updateCell (@PathVariable long buildingId,@PathVariable long fileId,@PathVariable long tableId,
			@RequestParam int columnIndex,@RequestParam int rowIndex,@RequestBody Map<String,String> cellBody) { 
		String cell = cellBody.get("contents");
		TempTableData updatedCell = dataService.updateCell(tableId,columnIndex,rowIndex,cell);
		return ResponseEntity.status(HttpStatus.CREATED).body(updatedCell); 
	}	   
	   
	@PatchMapping("buildings/{buildingId}/files/{fileId}/tables/{tableId}/columns")//열의 이름 업데이트 
	public ResponseEntity<String> updateColumnName (@PathVariable long buildingId, @PathVariable long fileId,@PathVariable long tableId
			,@RequestParam int columnIndex,@RequestBody @Valid NameDto nameDto) {
		dataService.updateColumnName(tableId, columnIndex,nameDto);
		return ResponseEntity.ok("Column name updated successfully.");
	}
	//delete
	//열 삭제하고 아래있는거 땡기기
	@DeleteMapping("buildings/{buildingId}/files/{fileId}/tables/{tableId}/columns") 
	public ResponseEntity<String> DeleteColumn(@PathVariable long buildingId,@PathVariable long fileId,@PathVariable long tableId,@RequestParam int columnIndex) {
		dataService.deleteColumn(tableId,columnIndex);
		return ResponseEntity.ok().build(); 
	}
	
	//행 삭제하고 아래있는거 땡기기 
	@DeleteMapping("buildings/{buildingId}/files/{fileId}/tables/{tableId}/rows") 
	public ResponseEntity<String> DeleteRow(@PathVariable long buildingId, @PathVariable long fileId,@PathVariable long tableId,@RequestParam int rowIndex) {
		dataService.deleteRow(tableId,rowIndex);
		return ResponseEntity.ok().build();
	}
	 
	
}

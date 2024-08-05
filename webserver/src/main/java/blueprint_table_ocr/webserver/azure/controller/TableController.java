package blueprint_table_ocr.webserver.azure.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import blueprint_table_ocr.webserver.azure.Dto.NameDto;
import blueprint_table_ocr.webserver.azure.service.TableService;
import blueprint_table_ocr.webserver.datapart.TableDoc;
import jakarta.validation.Valid;

@RestController
public class TableController {
	 private TableService tableService;
	 public TableController(TableService tableService) {
		 this.tableService = tableService;
	 }
	////create
	@PostMapping("buildings/{buildingId}/files/{fileId}/tables")//테이블 새로 만들기-빈테이블과 첫 버전
	public ResponseEntity<Void> createTable (@PathVariable long buildingId,@PathVariable long fileId, @RequestBody @Valid NameDto Content) {
		tableService.createNewTable(fileId, Content.getName());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	 
	//read	
	@GetMapping("buildings/{buildingId}/files/{fileId}/tables")//해당 아이디를 가진 파일의 테이블 리스트 보여주기 
	public ResponseEntity<List<TableDoc>> listAlltables(@PathVariable long fileId) {	
		List<TableDoc> tableLists = tableService.findTableById(fileId);	
		 if (tableLists.isEmpty()) {
		        return ResponseEntity.noContent().build(); // 테이블이 없을 경우 HTTP 204 No Content 반환
		 }
		 return ResponseEntity.ok(tableLists);	   
	}
	
	//update
	@PatchMapping("buildings/{buildingId}/files/{fileId}/tables/{tableId}")//원하는 테이블 이름 업데이트 *버전 안남기기
	public ResponseEntity<TableDoc> updateTableName (@PathVariable long fileId,@PathVariable long tableId ,@RequestBody @Valid NameDto nameDto) { 
		TableDoc updatedTable = tableService.updateTableName(tableId, nameDto.getName());
		return ResponseEntity.ok(updatedTable);
	}
	
	//테이블 삭제하기 일단 cascade remove적용,기록 안남기기
	@DeleteMapping("buildings/{buildingId}/files/{fileId}/tables/{tableId}") 
	public ResponseEntity<Void> DeletetableFromDatabase (@PathVariable long buildingId, @PathVariable long fileId, @PathVariable long tableId) {
		tableService.deleteTable(tableId);
		return ResponseEntity.noContent().build();
	}
	   
	

}

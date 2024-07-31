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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import blueprint_table_ocr.webserver.azure.Dto.FileInfoDto;
import blueprint_table_ocr.webserver.azure.service.FileService;
import blueprint_table_ocr.webserver.datapart.OwnerFile;
import jakarta.validation.Valid;

@RestController
public class FileController {
   private FileService fileService;
	   
	   public FileController(FileService fileService) {
	     
	      this.fileService = fileService;
	   }
	
	 //upload and save file
	   @PostMapping("buildings/{buildingId}/files")
	   public ResponseEntity<String> saveExcelDatabase(@RequestPart("file") MultipartFile file,@PathVariable Long buildingId,@RequestPart("fileInfo") @Valid FileInfoDto fileInfo) {
	       try {
	    	   fileService.saveFile(file,buildingId,fileInfo);
	    	   return ResponseEntity.status(HttpStatus.CREATED).body("File uploaded and data saved to database successfully.");
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file and save data to database.");
	        }
	   }
	   
	   //read files of specific building
		@GetMapping("buildings/{buildingId}/files")
		public ResponseEntity<List<OwnerFile>> listfiles(@PathVariable Long buildingId) {
			List<OwnerFile> filelist = fileService.getFileofBuilding(buildingId);
			return ResponseEntity.ok(filelist);
		}
		
		//update file info
		@PatchMapping("buildings/{buildingId}/files/{fileId}")//원하는 파일 이름 업데이트 *PATCH/{{baseUrl}}/files/:fileId
		public ResponseEntity<OwnerFile> updateFileName (@PathVariable long buildingId,@PathVariable long fileId ,@RequestBody @Valid FileInfoDto fileInfo) { 
			OwnerFile updatedFile = fileService.updateFileInfo(fileId,fileInfo);
			return ResponseEntity.ok(updatedFile);
		}
		
	   //delete file
		@DeleteMapping("buildings/{buildingId}/files/{fileId}")//파일 삭제하기 *DELETE/ {{baseUrl}}/files/:fileId
		public ResponseEntity<Void> DeleteFromDatabase (@PathVariable long buildingId,@PathVariable long fileId) {
			fileService.deleteFile(fileId);
			return ResponseEntity.noContent().build();
		}

}

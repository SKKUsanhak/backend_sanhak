package blueprint_table_ocr.webserver.azure.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FileInfoDto {
	@NotBlank
	private String fileName;

 
	@Size(max = 300)
	private String note;


	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public String getNote() {
		return note;
	}


	public void setNote(String note) {
		this.note = note;
	}
	  


}

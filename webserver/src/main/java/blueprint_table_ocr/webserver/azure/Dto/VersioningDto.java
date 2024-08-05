package blueprint_table_ocr.webserver.azure.Dto;

import jakarta.validation.constraints.NotBlank;

public class VersioningDto {
	
	private String note;
	
	@NotBlank(message="버전을 반드시 입력하세요")
	private String version;

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}

package blueprint_table_ocr.webserver.azure.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FileInformationDto {
	@NotBlank
	private String fileName;
	@NotBlank
	private String address;
	@NotBlank
	private String buildingName;
	@Size(max = 300)
	private String note;
	public String getFilename() {
		return fileName;
	}
	public void setFilename(String filename) {
		this.fileName = filename;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getBuildingName() {
		return buildingName;
	}
	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}

}

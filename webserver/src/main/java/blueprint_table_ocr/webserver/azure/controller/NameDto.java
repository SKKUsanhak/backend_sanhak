package blueprint_table_ocr.webserver.azure.controller;

import jakarta.validation.constraints.NotBlank;

public class NameDto {
	@NotBlank(message = "파일 이름은 비워둘 수 없습니다")
    private String Name;

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

}

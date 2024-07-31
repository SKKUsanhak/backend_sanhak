package blueprint_table_ocr.webserver.azure.Dto;

import jakarta.validation.constraints.NotBlank;

public class BuildingDto {

	@NotBlank(message = "건물 이름은 비워둘 수 없습니다")	
	private String buildingName;
	@NotBlank(message = "주소는 비워둘 수 없습니다")
	private String address;
	private String totalArea;
	private Integer BasementFloors;
	private Integer GroundFloors;
	
	public String getBuildingName() {
		return buildingName;
	}
	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTotalArea() {
		return totalArea;
	}
	public void setTotalArea(String totalArea) {
		this.totalArea = totalArea;
	}
	public Integer getBasementFloors() {
		return BasementFloors;
	}
	public void setBasementFloors(Integer basementFloors) {
		BasementFloors = basementFloors;
	}
	public Integer getGroundFloors() {
		return GroundFloors;
	}
	public void setGroundFloors(Integer groundFloors) {
		GroundFloors = groundFloors;
	}
		
	
	

}

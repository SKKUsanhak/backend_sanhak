package blueprint_table_ocr.webserver.datapart;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Building {
	@Id
	@GeneratedValue
	private Long id;
	
	private String buildingName;
	private String address;
	private String totalArea;
	private Integer BasementFloors;
	private Integer GroundFloors;
	
	@OneToMany(mappedBy =  "buildingInfo",cascade = CascadeType.REMOVE)
	@JsonManagedReference
	@JsonIgnore
	private List<OwnerFile> fileList;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public List<OwnerFile> getFileList() {
		return fileList;
	}

	public void setFileList(List<OwnerFile> fileList) {
		this.fileList = fileList;
	}


	
}

package blueprint_table_ocr.webserver.datapart;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;

@Entity
public class OwnerFile {
	@Id
	@GeneratedValue
	private Long Id;
	
	private String fileName;
	private String Note;
	private LocalDateTime createTime;
	private LocalDateTime updateTime;
	
	@OneToMany(mappedBy =  "fileInfo",cascade = CascadeType.REMOVE)
	@JsonManagedReference
	@JsonIgnore
	private List<TableDoc> tableDoc;
	
	@ManyToOne
	@JsonBackReference//순환 참조 방지
	private Building buildingInfo;
	
	/*
	@ManyToOne
	@JsonBackReference//순환 참조 방지
	private UserData userInfo;
	*/
	@PrePersist
	protected void onCreate() {
		this.setCreateTime(LocalDateTime.now());
		this.setUpdateTime(LocalDateTime.now());
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<TableDoc> getTableDoc() {
		return tableDoc;
	}

	public void setTableDoc(List<TableDoc> tableDoc) {
		this.tableDoc = tableDoc;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}

	public Building getBuildingInfo() {
		return buildingInfo;
	}

	public void setBuildingInfo(Building buildingInfo) {
		this.buildingInfo = buildingInfo;
	}

	public String getNote() {
		return Note;
	}

	public void setNote(String note) {
		Note = note;
	}


}

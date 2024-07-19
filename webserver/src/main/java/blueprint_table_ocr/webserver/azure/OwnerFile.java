package blueprint_table_ocr.webserver.azure;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;

@Entity
public class OwnerFile {
	@Id
	@GeneratedValue
	private Long Id;
	
	private String fileName;
	
	@OneToMany(mappedBy =  "fileInfo",cascade = CascadeType.REMOVE)
	@JsonManagedReference
	@JsonIgnore
	private List<TableDoc> tableDoc;
	
	private LocalDateTime createTime= LocalDateTime.now();
	
	@PrePersist
	    protected void onCreate() {
	        this.setCreateTime(LocalDateTime.now());
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

}

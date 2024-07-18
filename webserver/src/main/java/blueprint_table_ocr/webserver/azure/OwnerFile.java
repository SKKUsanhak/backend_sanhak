package blueprint_table_ocr.webserver.azure;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class OwnerFile {
	@Id
	@GeneratedValue
	private Integer Id;
	
	private String fileName;
	
	@OneToMany(mappedBy =  "fileInfo",cascade = CascadeType.REMOVE)
	@JsonManagedReference
	@JsonIgnore
	private List<TableDoc> tableDoc;

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
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

}

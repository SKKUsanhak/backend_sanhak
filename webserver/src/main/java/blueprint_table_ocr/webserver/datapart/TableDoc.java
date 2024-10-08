package blueprint_table_ocr.webserver.datapart;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class TableDoc {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String tableTitle;

	
	@OneToMany(mappedBy="tableInfo")
	@JsonManagedReference
	@JsonIgnore
	private List<TempTableData> temptableDatas;
	
	@OneToMany(mappedBy="tableInfo")
	@JsonManagedReference
	private List<DataVersionControl> dataVersionList;
	
	@ManyToOne
	@JsonBackReference//순환 참조 방지
	private OwnerFile fileInfo;

	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTableTitle() {
		return tableTitle;
	}

	public void setTableTitle(String tableTitle) {
		this.tableTitle = tableTitle;
	}

	public OwnerFile getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(OwnerFile fileInfo) {
		this.fileInfo = fileInfo;
	}


	
}

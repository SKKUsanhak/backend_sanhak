package blueprint_table_ocr.webserver.azure;

import java.util.List;

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
	private List<TableData> tableDatas;
	
	@ManyToOne//(cascade = CascadeType.PERSIST)
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

	public List<TableData> getTableDatas() {
		return tableDatas;
	}

	public void setTableDatas(List<TableData> tableDatas) {
		this.tableDatas = tableDatas;
	}

	public OwnerFile getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(OwnerFile fileInfo) {
		this.fileInfo = fileInfo;
	}


	
}

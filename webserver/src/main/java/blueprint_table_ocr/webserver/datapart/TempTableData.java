package blueprint_table_ocr.webserver.datapart;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class TempTableData{
	@Id
	@GeneratedValue
	private Long id;
	
	private Integer rowNumber;
	private Integer columnNumber;
	private String columnName;
	private String contents;
	
	@ManyToOne
	@JsonBackReference
	private TableDoc tableInfo;
	
	@ManyToOne
	@JsonBackReference
	private DataVersionControl versionInfo;
	

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public TableDoc getTableInfo() {
		return tableInfo;
	}

	public void setTableInfo(TableDoc tableInfo) {
		this.tableInfo = tableInfo;
	}

	public Integer getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(Integer rowNumber) {
		this.rowNumber = rowNumber;
	}


	public Integer getColumnNumber() {
		return columnNumber;
	}

	public void setColumnNumber(Integer columnNumber) {
		this.columnNumber = columnNumber;
	}

	public DataVersionControl getVersionInfo() {
		return versionInfo;
	}

	public void setVersionInfo(DataVersionControl versionInfo) {
		this.versionInfo = versionInfo;
	}


	
	
}

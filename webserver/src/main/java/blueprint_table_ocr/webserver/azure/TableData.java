package blueprint_table_ocr.webserver.azure;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class TableData {
	@Id
	@GeneratedValue
	private Integer id;
	
	private Integer rowNumber;
	private String columnName;
	private String contents;
	
	@ManyToOne(cascade = CascadeType.PERSIST)
	private TableDoc tableInfo;

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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

	
	
	
	
	
	
	
	
}

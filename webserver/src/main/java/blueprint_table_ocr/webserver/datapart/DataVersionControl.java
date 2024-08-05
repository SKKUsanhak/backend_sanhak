package blueprint_table_ocr.webserver.datapart;

import java.time.LocalDateTime;
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
import jakarta.persistence.PrePersist;

@Entity
public class DataVersionControl {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String note;
	private String version;
	
	private LocalDateTime updateTime;
	@PrePersist
	protected void onCreate() {
		this.setUpdateTime(LocalDateTime.now());
	}
	
	@ManyToOne
	@JsonBackReference
	private TableDoc tableInfo;
	
	@OneToMany(mappedBy="versionInfo")
	@JsonManagedReference
	@JsonIgnore
	private List<TempTableData> temptableDatas;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}

	public TableDoc getTableInfo() {
		return tableInfo;
	}

	public void setTableInfo(TableDoc tableInfo) {
		this.tableInfo = tableInfo;
	}

	public List<TempTableData> getTemptableDatas() {
		return temptableDatas;
	}

	public void setTemptableDatas(List<TempTableData> temptableDatas) {
		this.temptableDatas = temptableDatas;
	}

}

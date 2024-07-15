package com.example.DatabaseTest.DataPart;

import java.util.List;

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
	
	@OneToMany(mappedBy =  "fileInfo")
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

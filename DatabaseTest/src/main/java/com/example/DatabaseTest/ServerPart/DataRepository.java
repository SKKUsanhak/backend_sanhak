package com.example.DatabaseTest.ServerPart;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.DatabaseTest.DataPart.TableData;



public interface DataRepository extends JpaRepository<TableData,Long> {
	List<TableData> findByContents(String contents);
	List<TableData> findByrowNumber(Integer i);

}

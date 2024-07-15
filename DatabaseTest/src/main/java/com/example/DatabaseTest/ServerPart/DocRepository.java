package com.example.DatabaseTest.ServerPart;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.DatabaseTest.DataPart.TableDoc;



public interface DocRepository extends JpaRepository<TableDoc,Long> {

}

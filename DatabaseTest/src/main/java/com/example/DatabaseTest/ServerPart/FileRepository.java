package com.example.DatabaseTest.ServerPart;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.DatabaseTest.DataPart.OwnerFile;

public interface FileRepository extends JpaRepository<OwnerFile,Long> {

}

package com.example.DatabaseTest.ServerPart;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.DatabaseTest.DataPart.TableData;



@RestController
public class ExcelController {
	
	@Autowired
	private ExcelService excelService;
	public ExcelController(ExcelService excelService) {
		super();
		this.excelService = excelService;
	}


	@PostMapping("/save-excel-db")
	public String SaveExcelDatabase (@RequestParam("excelfile") MultipartFile file) {
		try {
			excelService.saveDb(file);
			return "File uploaded and data saved to database successfully.";
			} 
		catch (Exception e) {
			e.printStackTrace();
			return "Failed to upload file and save data to database.";
		}
	}
	
	
	@PostMapping("/find-excel-db")
	@ResponseBody
	public List<TableData> FindFromDatabase (@RequestParam String kw) {
		List<TableData> results = excelService.findFullRow(kw);
		 return results; // 결과를 JSON 형식으로 반
	}
	
}

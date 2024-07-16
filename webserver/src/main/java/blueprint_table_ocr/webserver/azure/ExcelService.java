package blueprint_table_ocr.webserver.azure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExcelService {
	
	private DataRepository dataRepository;
	private DocRepository docRepository;	
	private FileRepository fileRepository;
	
	public ExcelService(DataRepository dataRepository, DocRepository docRepository,FileRepository fileRepository) {
		super();
		this.dataRepository = dataRepository;
		this.docRepository = docRepository;
		this.fileRepository = fileRepository;
	}
	
	
	public void saveDb(MultipartFile file)  throws IOException{
		try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
			OwnerFile ownerFile = new OwnerFile(); 
			ownerFile.setFileName(file.getName());
			fileRepository.save(ownerFile);
			 
			
			for(int i=0;i<workbook.getNumberOfSheets();i++) {
				Sheet sheet = workbook.getSheetAt(i);//첫번째 시트 읽기
				List<TableData> tableDataList = new ArrayList<>();//리스트에 하나의 테이블의 데이터 다 저장
				TableDoc newTableDoc  = new TableDoc();
				for (Row row : sheet) {
					 
		            if (row.getRowNum() == 0) { 
		                newTableDoc.setTableTitle(row.getCell(0).getStringCellValue());
		                newTableDoc.setFileInfo(ownerFile);
		                docRepository.save(newTableDoc);
		                continue;
		            	  // 첫 번째 행은 헤더이므로 건너뜁니다.
		            }
		            if (newTableDoc == null) {
		                throw new IllegalStateException("Header row must be processed before data rows.");
		            }
		            
		            Row firstnamerow = sheet.getRow(1);
		            if (row.getRowNum() == 1) {
		            	continue;
		            }
		            	
		            
		            for (Cell cell : row) {
			            TableData tableData = new TableData();
			            tableData.setRowNumber(row.getRowNum());//열 설정
			            tableData.setColumnName(firstnamerow.getCell(cell.getColumnIndex()).getStringCellValue());//열 이름
			            tableData.setContents(cell.getStringCellValue());//내용
			            tableData.setTableInfo(newTableDoc);//테이블 설정
			            //tableDataList.add(tableData);
			            
			            tableDataList.add(tableData);//한 셀을 저장
		            }
		        }
				 dataRepository.saveAll(tableDataList);
			
			}
		}
		
		
	}
	
	
	
	public List<TableData> findFullRow (String kw){// 잘 안됨
		List<TableData> keywordList =dataRepository.findByContents(kw);
		
		Set<Integer> rowNum = new HashSet<>();//여기에 행 정보 저장
		
		 for (TableData tableData : keywordList) {
	            if (tableData != null) {
	                rowNum.add(tableData.getRowNumber());
	            }
	     }
		  
		//List<List<TableData>> listOfLists = new ArrayList<>();
		 List<TableData> finalList = new ArrayList<>();
		 for (Integer i: rowNum) {
			 finalList =dataRepository.findByrowNumber(i);
			 
		 }
		
		
		return finalList;
		
	}

}

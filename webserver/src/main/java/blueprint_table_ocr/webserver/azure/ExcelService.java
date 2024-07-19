package blueprint_table_ocr.webserver.azure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import blueprint_table_ocr.webserver.azure.AzureController.CreateRequest;

@Service
public class ExcelService {
	
	private DataRepository dataRepository;
	private TempDataRepository tempdataRepository;
	private DocRepository docRepository;	
	private FileRepository fileRepository;
	
	public ExcelService(DataRepository dataRepository, DocRepository docRepository,FileRepository fileRepository, TempDataRepository tempdataRepository) {
		super();
		this.dataRepository = dataRepository;
		this.docRepository = docRepository;
		this.fileRepository = fileRepository; 
		this.tempdataRepository = tempdataRepository;
	}
	
	
	public void saveDb(MultipartFile file, String fileName)  throws IOException{///////////////////////데이터베이스에 삽입하는 코드
		try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
			OwnerFile ownerFile = new OwnerFile(); 
			ownerFile.setFileName(fileName);
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
			            //if(cell.getColumnIndex()==0) tableData.setFirstColumn(true);
			            tableData.setColumnNumber(cell.getColumnIndex());
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
	
	///
	public void saveTempDb(MultipartFile file, String fileName)  throws IOException{///////////////////////데이터베이스에 삽입하는 코드 for임시데이터
		try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
			OwnerFile ownerFile = new OwnerFile(); 
			ownerFile.setFileName(fileName);
			fileRepository.save(ownerFile);
			 
			
			for(int i=0;i<workbook.getNumberOfSheets();i++) {
				Sheet sheet = workbook.getSheetAt(i);//첫번째 시트 읽기
				List<TempTableData> temptableDataList = new ArrayList<>();//리스트에 하나의 테이블의 데이터 다 저장
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
			            TempTableData temptableData = new TempTableData();
			            
			            temptableData.setColumnNumber(cell.getColumnIndex());
			            temptableData.setRowNumber(row.getRowNum());//열 설정
			            temptableData.setColumnName(firstnamerow.getCell(cell.getColumnIndex()).getStringCellValue());//열 이름
			            temptableData.setContents(cell.getStringCellValue());//내용
			            temptableData.setTableInfo(newTableDoc);//테이블 설정
			          
			            
			            temptableDataList.add(temptableData);//한 셀을 저장
		            }
		        }
				 tempdataRepository.saveAll(temptableDataList);
			
			}
		}
		
		
	}
	
	
 
///delete
	public void deleteFile(long id) {///////////////////////////////////////////////////원하는 파일을 삭제하는 코드
		fileRepository.deleteById(id);
		
	}

///read
	public List<OwnerFile> findAllFile() {/////////////////////////////////////전체 파일 보여주는 코드
		 
		return fileRepository.findAll();
	}


	public List<TableDoc> findTableById(long id) {//해당 파일의 모든 테이블 보여주기
		return docRepository.findByFileInfoId(id);
	}


	public List<TempTableData> findTempDataById(long tableId) {//특정 파일,테이블의 데이터들 보여주기
		List<TempTableData> tempdatalists = tempdataRepository.findByTableInfoId(tableId).get();
		
		return tempdatalists;
	}
////update

	public void updateTempCell(long cellid, String contents) {//temp단일 셀 수정하기 
		Optional<TempTableData> temp = tempdataRepository.findById(cellid);
		TempTableData tempCell = temp.get();
		tempCell.setContents(contents);
		tempdataRepository.save(tempCell);
		
	}


	public void updateTableName(long tableid, String contents) {//테이블 이름 수정하기
		Optional<TableDoc> temp = docRepository.findById(tableid);
		TableDoc tableDoc = temp.get();
		tableDoc.setTableTitle(contents);
		docRepository.save(tableDoc);
		
	}


	public void updateColumnName(long tableid, int columnnumber, String contents) {//열 이름 수정하기
		List<TempTableData> dataoftable = tempdataRepository.findByTableInfoId(tableid).get();
		for(TempTableData cell :dataoftable) {
			if(cell.getColumnNumber() == columnnumber) {
				cell.setColumnName(contents);
				tempdataRepository.save(cell);
			}
		}
	}
///create
	public void createNewTable(long fileid, String tablename) {
		Optional<OwnerFile> file = fileRepository.findById(fileid);
		OwnerFile fileinfo = file.get();
		TableDoc newTable = new TableDoc();
		newTable.setFileInfo(fileinfo);
		newTable.setTableTitle(tablename);
		docRepository.save(newTable);
		
	}


	public String createNewCell(CreateRequest createinfo) {
		List<TempTableData> tempdatalists = tempdataRepository.findByTableInfoId(createinfo.tableId).get();
		for(TempTableData cell:tempdatalists) {
			if(cell.getRowNumber() == createinfo.row && cell.getColumnNumber() == createinfo.column) {
				return "cell is already present";
			}
		}
		
		
		TableDoc tableinfo = docRepository.findById(createinfo.tableId).get();
		TempTableData newTempData = new TempTableData();
		newTempData.setColumnNumber(createinfo.column);
		newTempData.setRowNumber(createinfo.row);
		newTempData.setContents(createinfo.contents);
		newTempData.setTableInfo(tableinfo);
		//열 이름 얻기
		 
		int flag=1;
		for(TempTableData x:tempdatalists) {
			if(x.getColumnNumber()==createinfo.column) {
				String Cname = x.getColumnName();
				newTempData.setColumnName(Cname);
				flag=0;
				break;
			}
		}
		if(flag==1)newTempData.setColumnName("column is not present");
		tempdataRepository.save(newTempData);
		
		return "cell is created";
		  
		
	}


	public String createNewColumn(long tableid, int colindex, String contents) {
		TableDoc tableinfo = docRepository.findById(tableid).get();
		TempTableData newTempData = new TempTableData();
		newTempData.setTableInfo(tableinfo);
		newTempData.setRowNumber(-1);
		newTempData.setContents(null);
		newTempData.setColumnNumber(colindex);
		newTempData.setColumnName(contents);
		tempdataRepository.save(newTempData);
		return "new column created";
		
	}

	//data db에 저장만 하는거
	
	public void saveFinalDb() {
		 List<TempTableData> tempDataList = tempdataRepository.findAll();		
		 List<TableData> tableDataList = new ArrayList<>();
		 for(TempTableData cell : tempDataList) {
			 TableData tableData = new TableData();
			 tableData.setColumnName(cell.getColumnName());
			 tableData.setColumnNumber(cell.getColumnNumber());
			 tableData.setRowNumber(cell.getRowNumber());
			 tableData.setTableInfo(cell.getTableInfo());
			 tableData.setContents(cell.getContents());
			 tableDataList.add(tableData);
		 }
		 dataRepository.saveAll(tableDataList);
	}


	public boolean isFinalTableEmpty() {//파이널 데이터가 비었는지 확인
		 return dataRepository.countAll() == 0;
		
	}


	public void updateFinalCell(long cellid, String contents) {
		Optional<TableData> temp = dataRepository.findById(cellid);
		TableData finalCell = temp.get();
		finalCell.setContents(contents);
		dataRepository.save(finalCell);
		
		
	}
    

}



/*
public List<TableData> findFullRow (String kw){// 잘 됨 /////////////////////////////////////////기호로 검색하는 코드
	List<TableData> keywordList = dataRepository.findByContentsContaining(kw);
	List<TableData> filteredKeywordList = keywordList.stream()
            .filter(TableData::isFirstColumn)
            .collect(Collectors.toList());// 키워드 갖고있는것중 기호 열인 것만 남김
    if (keywordList.isEmpty()) {
        // kw에 해당하는 데이터가 없으면 빈 리스트 반환
        return Collections.emptyList();
    }
   
    
    
    return filteredKeywordList.stream()//filteredKeywordList에 있는 기호들마다 아래의 로직 반복
            .flatMap(data -> {
                int rowNum = data.getRowNumber();//검색한 키워드가 포함된 기호의 행숫자 가져오기
                TableDoc tableDoc = data.getTableInfo();//해당 기호의 테이블 인포 
                Long tableNum = tableDoc.getId();//테이블 인포에서 테이블 id뽑기

                System.out.println("Row Number: " + rowNum);
                System.out.println("Table Number: " + tableNum);

                List<TableData> answerList = dataRepository.findByrowNumber(rowNum);//rownumber로 해당 행에 들어가는 정보들 찾기
                return answerList.stream()
                                 .filter(answer -> answer.getTableInfo().getId().equals(tableNum));//그 정보들중 같은 테이블의 정보만 남기기 
            })//-> 한 행을 구한거임(한행 = 여러 객체가 포함된 list)
            .collect(Collectors.toList());
}

*/

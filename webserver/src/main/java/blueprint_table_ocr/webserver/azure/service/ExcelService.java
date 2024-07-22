package blueprint_table_ocr.webserver.azure.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import blueprint_table_ocr.webserver.azure.AzureController.UpdateRequest;
import blueprint_table_ocr.webserver.azure.Repository.DataRepository;
import blueprint_table_ocr.webserver.azure.Repository.DocRepository;
import blueprint_table_ocr.webserver.azure.Repository.FileRepository;
import blueprint_table_ocr.webserver.azure.Repository.TempDataRepository;
import blueprint_table_ocr.webserver.datapart.Data;
import blueprint_table_ocr.webserver.datapart.OwnerFile;
import blueprint_table_ocr.webserver.datapart.TableData;
import blueprint_table_ocr.webserver.datapart.TableDoc;
import blueprint_table_ocr.webserver.datapart.TempTableData;

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
	
	
	
	
//////////////////////데이터베이스에 삽입하는 코드 for임시데이터
	public void saveTempDb(MultipartFile file, String fileName)  throws IOException{
		try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
			OwnerFile ownerFile = new OwnerFile(); 
			ownerFile.setFileName(fileName);
			fileRepository.save(ownerFile);
			 
			
			for(int i=0;i<workbook.getNumberOfSheets();i++) {
				Sheet sheet = workbook.getSheetAt(i);//첫번째 시트 읽기
				List<TempTableData> temptableDataList = new ArrayList<>();//리스트에 하나의 테이블의 데이터 다 저장
				TableDoc newTableDoc  = new TableDoc();
				newTableDoc.setFinalData(false);
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
	public void deleteFile(long id) {//원하는 파일을 삭제하는 코드
		fileRepository.deleteById(id);
		
	}
	
	public void deleteTempRow(long tableid, int rowindex) {
		List<TempTableData> tempdatalist = tempdataRepository.findByTableInfoId(tableid).get();
		for(TempTableData cell:tempdatalist) {
			if(cell.getRowNumber()==rowindex) {
				tempdataRepository.deleteById(cell.getId());
			}
		}
		
		for(TempTableData cell:tempdatalist) {
			if(cell.getRowNumber()>rowindex) {
				int currentrownumber = cell.getRowNumber();
				cell.setRowNumber(currentrownumber-1);
				tempdataRepository.save(cell);
				
			}
		}
		
	}
	

	public void deleteTempColumn(long tableid, int columnindex) {
		List<TempTableData> tempdatalist = tempdataRepository.findByTableInfoId(tableid).get();
		for(TempTableData cell:tempdatalist) {
			if(cell.getColumnNumber()==columnindex) {
				tempdataRepository.deleteById(cell.getId());
			}
		}
		
		for(TempTableData cell:tempdatalist) {
			if(cell.getColumnNumber()>columnindex) {
				int currentcolumnnumber = cell.getColumnNumber();
				cell.setColumnNumber(currentcolumnnumber-1);
				tempdataRepository.save(cell);
				
			}
		}
		
		
		
	}
	

///read
	public List<OwnerFile> findAllFile() {/////////////////////////////////////전체 파일 보여주는 코드
		 
		return fileRepository.findAll();
	}


	public List<TableDoc> findTableById(long id) {//해당 파일의 모든 테이블 보여주기
		return docRepository.findByFileInfoId(id);
	}


	public List<? extends Data> findTempDataById(long tableId) {//특정 파일,테이블의 데이터들 보여주기
		List<? extends Data> tempdatalists = tempdataRepository.findByTableInfoId(tableId).get();
		return tempdatalists;
	}

	public List<? extends Data> findDataById(long tableId) {
		List<? extends Data> datalists = dataRepository.findByTableInfoId(tableId).get();
		return datalists;
	}



////update
	public void updateTempCell(UpdateRequest updateinfo) {
		List<TempTableData> tempdatalists = tempdataRepository.findByTableInfoId(updateinfo.tableId).get();
		for(TempTableData cell:tempdatalists) {
			if(cell.getRowNumber() == updateinfo.row && cell.getColumnNumber() == updateinfo.column) {
				cell.setContents(updateinfo.contents);
				tempdataRepository.save(cell);
			}
		}
		
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
		TempTableData basicCell = new TempTableData();
		basicCell.setColumnName("");
		basicCell.setColumnNumber(0);
		basicCell.setContents("");
		basicCell.setRowNumber(2);
		basicCell.setTableInfo(newTable);
		tempdataRepository.save(basicCell);
	}
	
	public String createNewColumn(long tableid, int colindex, String contents) {
		TableDoc tableinfo = docRepository.findById(tableid).get();
		List<TempTableData> tempList = tempdataRepository.findByTableInfoId(tableid).get();
		int Max=-1;
		for(TempTableData cell: tempList) {
			if(cell.getRowNumber()>Max) Max=cell.getRowNumber();
		}
		
		List<TempTableData> tableDataList = new ArrayList<>();//이 리스트에 저장
		for(int i=2;i<=Max;i++) {
			TempTableData tableData = new TempTableData();
			tableData.setColumnName(contents);
			 tableData.setColumnNumber(colindex);
			 tableData.setRowNumber(i);
			 tableData.setTableInfo(tableinfo);
			 tableData.setContents("");
			 tableDataList.add(tableData);
		}
		tempdataRepository.saveAll(tableDataList);
		
		return "new column created";
		
	}
	
	
	public String createNewRow(long tableid, int rowindex) {//새로운 빈 행 만들기
		TableDoc tableinfo = docRepository.findById(tableid).get();
		List<TempTableData> tempList = tempdataRepository.findByTableInfoId(tableid).get();
		int Max=-1;
		Map<Integer, String> columnMap = new HashMap<>();
		for(TempTableData cell: tempList) {
			if(cell.getColumnNumber()>Max) Max=cell.getColumnNumber();
			columnMap.put(cell.getColumnNumber(), cell.getColumnName());
		}
		
		
		List<TempTableData> tableDataList = new ArrayList<>();
		for(int i=0;i<=Max;i++) {
			TempTableData tableData = new TempTableData();
			tableData.setColumnName(columnMap.get(i));
			tableData.setColumnNumber(i);
			tableData.setRowNumber(rowindex);
			tableData.setTableInfo(tableinfo);
			tableData.setContents("");
			tableDataList.add(tableData);
		}
		tempdataRepository.saveAll(tableDataList);
		
		
		
		return "new row created";
	}
    
//data db에 저장만 하는거 finaldb part
	
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




	public void saveToFinalTable(long tableid) {
		List<TempTableData> tempDataList = tempdataRepository.findByTableInfoId(tableid).get();
		List<TableData> tableDataList = new ArrayList<>();
		for(TempTableData cell : tempDataList) {//final로 옮기기
			TableData tableData = new TableData();
			tableData.setColumnName(cell.getColumnName());
			tableData.setColumnNumber(cell.getColumnNumber());
			tableData.setRowNumber(cell.getRowNumber());
			tableData.setTableInfo(cell.getTableInfo());
			tableData.setContents(cell.getContents());
			tableDataList.add(tableData);
		}
		dataRepository.saveAll(tableDataList);
		
		for(TempTableData cell : tempDataList) {//temp에서 삭제
			tempdataRepository.deleteById(cell.getId());
		}
		
		
		
				
		TableDoc tableobject = docRepository.findById(tableid).get();
		tableobject.setFinalData(true);
		docRepository.save(tableobject);
		 
		
	}
	
	public boolean isFinalTable(long tableId) {
		if(docRepository.findById(tableId).get().isFinalData()==true) return true;
		return false;
	}



 
}



 
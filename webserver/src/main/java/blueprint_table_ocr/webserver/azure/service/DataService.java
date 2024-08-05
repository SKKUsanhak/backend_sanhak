package blueprint_table_ocr.webserver.azure.service;

import java.io.IOException;
import java.time.LocalDateTime;
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
	
	
	
 
///delete
	

	public void deleteTable(long tableid) {
		if(isFinalTable(tableid)==true) {
			List<TableData> datalist = dataRepository.findByTableInfoId(tableid).get();
			for(TableData cell: datalist) {//참조관계 끊기
				cell.setTableInfo(null);
				dataRepository.save(cell);
			}
			docRepository.deleteById(tableid);	
		}
		else {
			docRepository.deleteById(tableid);	
		}
		
		
	}

	
	public void deleteRow(long tableid, int rowindex) {
		if(isFinalTable(tableid)==true) {
			List<TableData> datalist = dataRepository.findByTableInfoId(tableid).get();
			for(TableData cell:datalist) {
				if(cell.getRowNumber()==rowindex) {
					dataRepository.deleteById(cell.getId());
				}
			}
			
			for(TableData cell:datalist) {
				if(cell.getRowNumber()>rowindex) {
					int currentrownumber = cell.getRowNumber();
					cell.setRowNumber(currentrownumber-1);
					dataRepository.save(cell);
					
				}
			}
		}
		else {
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
		
	}
	

	public void deleteColumn(long tableid, int columnindex) {
		if(isFinalTable(tableid)==true) {
			List<TableData> datalist = dataRepository.findByTableInfoId(tableid).get();
			for(TableData cell:datalist) {
				if(cell.getColumnNumber()==columnindex) {
					dataRepository.deleteById(cell.getId());
				}
			}
			
			for(TableData cell:datalist) {
				if(cell.getColumnNumber()>columnindex) {
					int currentcolumnnumber = cell.getColumnNumber();
					cell.setColumnNumber(currentcolumnnumber-1);
					dataRepository.save(cell);
					
				}
			}
		}
		else {
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
		
		
		
	}
	

///read//수정할것 없이 두 케이스에 대해 모두 작동

	public List<TableDoc> findTableById(long id) {//해당 파일의 모든 테이블 보여주기
		return docRepository.findByFileInfoId(id);
	}


	public List<? extends Data> findTempDataById(long tableId) {//특정 파일,테이블의 데이터들 보여주기 for temp data
		List<? extends Data> tempdatalists = tempdataRepository.findByTableInfoId(tableId).get();
		return tempdatalists;
	}

	public List<? extends Data> findDataById(long tableId) {//for final data
		List<? extends Data> datalists = dataRepository.findByTableInfoId(tableId).get();
		return datalists;
	}



////update

	public void updateDate(long fileid) {
		OwnerFile file = fileRepository.findById(fileid).get();
		file.setUpdateTime(LocalDateTime.now());
		fileRepository.save(file);
	}

	public void updateCell(long tableId,int columnIndex,int rowIndex,String contents) {
		if(isFinalTable(tableId)==true) {
			List<TableData> datalists = dataRepository.findByTableInfoId(tableId).get();
			for(TableData cell:datalists) {
				if(cell.getRowNumber() == rowIndex && cell.getColumnNumber() == columnIndex) {
					cell.setContents(contents);
					dataRepository.save(cell);
				}
			}
		}
		
		else {
			List<TempTableData> tempdatalists = tempdataRepository.findByTableInfoId(tableId).get();
			for(TempTableData cell:tempdatalists) {
				if(cell.getRowNumber() == rowIndex && cell.getColumnNumber() == columnIndex) {
					cell.setContents(contents);
					tempdataRepository.save(cell);
				}
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
		if(isFinalTable(tableid)==true) {
			List<TableData> dataoftable = dataRepository.findByTableInfoId(tableid).get();
			for(TableData cell :dataoftable) {
				if(cell.getColumnNumber() == columnnumber) {
					cell.setColumnName(contents);
					dataRepository.save(cell);
				}
			}
		}
		else {
			List<TempTableData> dataoftable = tempdataRepository.findByTableInfoId(tableid).get();
			for(TempTableData cell :dataoftable) {
				if(cell.getColumnNumber() == columnnumber) {
					cell.setColumnName(contents);
					tempdataRepository.save(cell);
				}
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
		newTable.setFinalData(false);
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
		if(isFinalTable(tableid)==true) {
			TableDoc tableinfo = docRepository.findById(tableid).get();
			List<TableData> tempList = dataRepository.findByTableInfoId(tableid).get();
			int Max=-1;
			for(TableData cell: tempList) {
				if(cell.getRowNumber()>Max) Max=cell.getRowNumber();
			}
			
			List<TableData> tableDataList = new ArrayList<>();//이 리스트에 저장
			for(int i=2;i<=Max;i++) {
				TableData tableData = new TableData();
				tableData.setColumnName(contents);
				 tableData.setColumnNumber(colindex);
				 tableData.setRowNumber(i);
				 tableData.setTableInfo(tableinfo);
				 tableData.setContents("");
				 tableDataList.add(tableData);
			}
			dataRepository.saveAll(tableDataList);
		}
		else {
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
			
		}
		
		return "new column created";
		
	}
	
	
	public String createNewRow(long tableid, int rowindex) {//새로운 빈 행 만들기
		if(isFinalTable(tableid)==true) {
			TableDoc tableinfo = docRepository.findById(tableid).get();
			List<TableData> dataList = dataRepository.findByTableInfoId(tableid).get();
			int Max=-1;
			Map<Integer, String> columnMap = new HashMap<>();
			for(TableData cell: dataList) {
				if(cell.getColumnNumber()>Max) Max=cell.getColumnNumber();
				columnMap.put(cell.getColumnNumber(), cell.getColumnName());
			}
			
			
			List<TableData> tableDataList = new ArrayList<>();
			for(int i=0;i<=Max;i++) {
				TableData tableData = new TableData();
				tableData.setColumnName(columnMap.get(i));
				tableData.setColumnNumber(i);
				tableData.setRowNumber(rowindex);
				tableData.setTableInfo(tableinfo);
				tableData.setContents("");
				tableDataList.add(tableData);
			}
			dataRepository.saveAll(tableDataList);
		}
		else {
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
			
		}
		
		
		
		return "new row created";
	}
    
//data 

	public void saveToFinalTable(long tableid) {//final table로 최초로 옮길 시 실행되는 매서드
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
		
		TableDoc tableobject = docRepository.findById(tableid).get();//true변경
		tableobject.setFinalData(true);
		docRepository.save(tableobject);
		 
		
	}
	
	public boolean isFinalTable(long tableId) {
		if(docRepository.findById(tableId).get().isFinalData()==true) return true;
		return false;
	}


 
}



 
package blueprint_table_ocr.webserver.azure.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import blueprint_table_ocr.webserver.azure.Repository.DataVersionControlRepository;
import blueprint_table_ocr.webserver.azure.Repository.DocRepository;
import blueprint_table_ocr.webserver.azure.Repository.FileRepository;
import blueprint_table_ocr.webserver.azure.Repository.TempDataRepository;
import blueprint_table_ocr.webserver.datapart.DataVersionControl;
import blueprint_table_ocr.webserver.datapart.OwnerFile;
import blueprint_table_ocr.webserver.datapart.TableDoc;
import blueprint_table_ocr.webserver.datapart.TempTableData;

@Service
public class TableService {
	private TempDataRepository tempdataRepository;
	private DocRepository docRepository;	
	private FileRepository fileRepository;
	private DataVersionControlRepository dvcRepository;
	
	public TableService(DocRepository docRepository,FileRepository fileRepository, TempDataRepository tempdataRepository,DataVersionControlRepository dvcRepository) {
		this.docRepository = docRepository;
		this.fileRepository = fileRepository; 
		this.tempdataRepository = tempdataRepository;
		this.dvcRepository=dvcRepository;
	}
	
	//create
	public void createNewTable(long fileid, String tablename) {
		Optional<OwnerFile> file = fileRepository.findById(fileid);
		OwnerFile fileinfo = file.get();
		TableDoc newTable = new TableDoc();
		newTable.setFileInfo(fileinfo);
		newTable.setTableTitle(tablename);
		docRepository.save(newTable);
		//초기 버전 만들기
		DataVersionControl version = new DataVersionControl();
		version.setNote("first version");
		version.setTableInfo(newTable);
		version.setVersion("v0.0.1");
		dvcRepository.save(version);
		//기본 셀 만들기
		TempTableData basicCell = new TempTableData(); 
		basicCell.setColumnName("");
		basicCell.setColumnNumber(0);
		basicCell.setContents("");
		basicCell.setRowNumber(2);
		basicCell.setTableInfo(newTable);
		basicCell.setVersionInfo(version);
		tempdataRepository.save(basicCell);
		 
	}
	
	//read
	public List<TableDoc> findTableById(long id) {//해당 파일의 모든 테이블 보여주기
		return docRepository.findByFileInfoId(id).get();
	}
	public String findTableName(long tableId) {
		TableDoc existingTable = docRepository.findById(tableId).get();
		String name = existingTable.getTableTitle();
		return name;
	}
	
	//update
	public TableDoc updateTableName(long tableId, String contents) {//테이블 이름 수정하기
		Optional<TableDoc> temp = docRepository.findById(tableId);
		TableDoc tableDoc = temp.get();
		tableDoc.setTableTitle(contents);
		return docRepository.save(tableDoc);
		
	}
	
	//delete
	public void deleteTable(long tableId) {
		List<TempTableData> datalist = tempdataRepository.findByTableInfoId(tableId).get();
			for(TempTableData cell: datalist) {//데이터와 참조관계 끊기
				cell.setTableInfo(null);
				tempdataRepository.save(cell);
			}
		List<DataVersionControl> versionlist = dvcRepository.findByTableInfoId(tableId).get();
			for(DataVersionControl version: versionlist) {//버전과 참조관계 끊기
			version.setTableInfo(null);
			dvcRepository.save(version);
		}
		//verison과 data는 연결되어있는 상태로 남음
		
		docRepository.deleteById(tableId);	
		
	}


}

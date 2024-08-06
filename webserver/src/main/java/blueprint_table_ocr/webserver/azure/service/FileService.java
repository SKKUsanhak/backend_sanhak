package blueprint_table_ocr.webserver.azure.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import blueprint_table_ocr.webserver.azure.Dto.FileInfoDto;
import blueprint_table_ocr.webserver.azure.Repository.BuildingRepository;
import blueprint_table_ocr.webserver.azure.Repository.DataVersionControlRepository;
import blueprint_table_ocr.webserver.azure.Repository.DocRepository;
import blueprint_table_ocr.webserver.azure.Repository.FileRepository;
import blueprint_table_ocr.webserver.azure.Repository.TempDataRepository;
import blueprint_table_ocr.webserver.datapart.DataVersionControl;
import blueprint_table_ocr.webserver.datapart.OwnerFile;
import blueprint_table_ocr.webserver.datapart.TableDoc;
import blueprint_table_ocr.webserver.datapart.TempTableData;

@Service
public class FileService {
	
	private TempDataRepository tempdataRepository;
	private DocRepository docRepository;	
	private FileRepository fileRepository;
	private BuildingRepository buildingRepository;
	private DataVersionControlRepository dvcRepository;
	
	public FileService( DocRepository docRepository,FileRepository fileRepository, TempDataRepository tempdataRepository,BuildingRepository buildingRepository,DataVersionControlRepository dvcRepository) {
		
		this.docRepository = docRepository;
		this.fileRepository = fileRepository; 
		this.tempdataRepository = tempdataRepository;
		this.buildingRepository = buildingRepository;
		this.dvcRepository = dvcRepository;
	}
	
	//create file
	public void saveFile(MultipartFile file,Long buildingId ,FileInfoDto fileInfo) throws IOException{
		try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
			OwnerFile ownerFile = new OwnerFile(); 
			ownerFile.setBuildingInfo(buildingRepository.findById(buildingId).get());
			ownerFile.setFileName(fileInfo.getFileName());
			ownerFile.setNote(fileInfo.getNote());
		
			fileRepository.save(ownerFile);//파일 저장
			 
			
			for(int i=0;i<workbook.getNumberOfSheets();i++) {
				Sheet sheet = workbook.getSheetAt(i);//첫번째 시트 읽기
				List<TempTableData> temptableDataList = new ArrayList<>();//리스트에 하나의 테이블의 데이터 다 저장
				TableDoc newTableDoc  = new TableDoc();
				DataVersionControl version = new DataVersionControl();
				for (Row row : sheet) {
					 
		            if (row.getRowNum() == 0) { 
		                newTableDoc.setTableTitle(row.getCell(0).getStringCellValue());
		                newTableDoc.setFileInfo(ownerFile);
		                docRepository.save(newTableDoc);//테이블 저장
		                
		                
		                 
						version.setNote("first version");
						version.setTableInfo(newTableDoc);
						version.setVersion("v0.0.1");
						dvcRepository.save(version);//초기 버전 저장
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
			            temptableData.setVersionInfo(version);
			          
			            
			            temptableDataList.add(temptableData);//한 셀을 저장
		            }
		        }
				
				 
				tempdataRepository.saveAll(temptableDataList);//데이터 저장
			   
			}
		}
		
		
	}
	
	//show file of specific building
	public List<OwnerFile> getFileofBuilding(Long buildingId) {	 
		return fileRepository.findByBuildingInfoId(buildingId).get();
	}
	
	//update file info
	public OwnerFile updateFileInfo(long fileId, FileInfoDto fileInfo) {
		OwnerFile file = fileRepository.findById(fileId).get();
		file.setFileName(fileInfo.getFileName());
		file.setNote(fileInfo.getNote());
		return fileRepository.save(file);
	}
	
	//delete file
	public void deleteFile(long fileId) {//원하는 파일을 삭제하는 코드
		//Todo 1.파일과 연결되어있는 테이블 구하기
		//2.각각의 테이블과 연결되어있는 참조 관계 끊기 
		//3.파일 삭제
		List<TableDoc> tableList = docRepository.findByFileInfoId(fileId).get();
		for(TableDoc table: tableList) {
			List<TempTableData> datalist = tempdataRepository.findByTableInfoId(table.getId()).get();
			for(TempTableData cell: datalist) {//데이터와 참조관계 끊기
				cell.setTableInfo(null);
				tempdataRepository.save(cell);
			}
			List<DataVersionControl> versionlist = dvcRepository.findByTableInfoId(table.getId()).get();
			for(DataVersionControl version: versionlist) {//버전과 참조관계 끊기
			version.setTableInfo(null);
			dvcRepository.save(version);
			}
		}
		OwnerFile deletedFile = fileRepository.findById(fileId).get();
		fileRepository.delete(deletedFile);
		
	}
}


package blueprint_table_ocr.webserver.azure.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import blueprint_table_ocr.webserver.azure.Dto.NameDto;
import blueprint_table_ocr.webserver.azure.Repository.DocRepository;
import blueprint_table_ocr.webserver.azure.Repository.TempDataRepository;
import blueprint_table_ocr.webserver.datapart.DataVersionControl;
import blueprint_table_ocr.webserver.datapart.TableDoc;
import blueprint_table_ocr.webserver.datapart.TempTableData;
import jakarta.transaction.Transactional;

@Service
public class DataService {
	private TempDataRepository tempdataRepository;
	private DocRepository docRepository;
	private DataVersionService dataVersionService;
	public DataService( DocRepository docRepository, TempDataRepository tempdataRepository,DataVersionService dataVersionService) {
		this.docRepository = docRepository;
		this.tempdataRepository = tempdataRepository;
		this.dataVersionService = dataVersionService;
	}
	
	
	///create
	//새로운 빈 열 만들기
	@Transactional
	public String createNewColumn(long tableId, int columnIndex, NameDto nameDto) {
		DataVersionControl latestVersion = dataVersionService.findLatestVersion(tableId);//이 테이블의 가장 최신 버전 찾기
		TableDoc tableInfo = docRepository.findById(tableId).get();
		List<TempTableData> latestDataList = tempdataRepository.findByVersionInfoId(latestVersion.getId()).get();//최신 버전을 가진 리스트 찾기
		
		int Max=-1;
		for(TempTableData cell: latestDataList) {
			if(cell.getRowNumber()>Max) Max=cell.getRowNumber();
		}
		
		List<TempTableData> newDataList = new ArrayList<>();//이 리스트에 저장
		for(int i=2;i<=Max;i++) {
			TempTableData newData = new TempTableData();
			newData.setColumnName(nameDto.getName());
			newData.setColumnNumber(columnIndex);
			newData.setRowNumber(i);
			newData.setTableInfo(tableInfo);
			newData.setContents("");
			newData.setVersionInfo(latestVersion);///////version info
			newDataList.add(newData);
		}
		tempdataRepository.saveAll(newDataList);
		
		return "new column created";
	}
		
	//새로운 빈 행 만들기
	@Transactional
	public String createNewRow(long tableId, int rowIndex) { 
		DataVersionControl latestVersion = dataVersionService.findLatestVersion(tableId);
		TableDoc tableinfo = docRepository.findById(tableId).get();
		List<TempTableData> latestDataList = tempdataRepository.findByVersionInfoId(latestVersion.getId()).get();
		int Max=-1;
		Map<Integer, String> columnMap = new HashMap<>();
		for(TempTableData cell: latestDataList) {
			if(cell.getColumnNumber()>Max) Max=cell.getColumnNumber();
			columnMap.put(cell.getColumnNumber(), cell.getColumnName());
		}
				
		List<TempTableData> newDataList = new ArrayList<>();			
		for(int i=0;i<=Max;i++) {
			TempTableData newData = new TempTableData();
			newData.setColumnName(columnMap.get(i));
			newData.setColumnNumber(i);
			newData.setRowNumber(rowIndex);
			newData.setTableInfo(tableinfo);
			newData.setContents("");
			newData.setVersionInfo(latestVersion);
			newDataList.add(newData);
		}
		tempdataRepository.saveAll(newDataList);
		
		return "new row created";
	}
	//read-다양한 version다 볼 수 있음
	public List<TempTableData> getDatas(long versionId) {
		List<TempTableData> latestDataList = tempdataRepository.findByVersionInfoId(versionId).get();
		return latestDataList;
	}
	//update
	//셀 수정하기
	/*
	@Transactional
	public TempTableData updateCell(long tableId,int columnIndex,int rowIndex,String contents) {
		DataVersionControl latestVersion = dataVersionService.findLatestVersion(tableId);
		TempTableData updatedCell = tempdataRepository.findByVersionInfoIdAndColumnNumberAndRowNumber(latestVersion.getId(),columnIndex,rowIndex).get();
		updatedCell.setContents(contents);
		return tempdataRepository.save(updatedCell);
	}*/
	
	@Transactional
	public TempTableData updateCell(long tableId,int columnIndex,int rowIndex,String contents) {
		DataVersionControl latestVersion = dataVersionService.findLatestVersion(tableId);
		List<TempTableData> latestDataList = tempdataRepository.findByVersionInfoId(latestVersion.getId()).get();
		for(TempTableData cell:latestDataList) {
			if(cell.getRowNumber() == rowIndex && cell.getColumnNumber() == columnIndex) {
				cell.setContents(contents);
				tempdataRepository.save(cell);
				return cell;
			}
		}
		return null;
	}
		
	//열 이름 수정하기
	@Transactional
	public void updateColumnName(long tableId, int columnIndex, NameDto nameDto) { 
		DataVersionControl latestVersion = dataVersionService.findLatestVersion(tableId);
		List<TempTableData> latestDataList = tempdataRepository.findByVersionInfoId(latestVersion.getId()).get();
			for(TempTableData cell :latestDataList) {
				if(cell.getColumnNumber() == columnIndex) {
					cell.setColumnName(nameDto.getName());
					tempdataRepository.save(cell);
				}
			}
	}
	
	//delete
	@Transactional
	public void deleteColumn(long tableId, int columnIndex) {
		DataVersionControl latestVersion = dataVersionService.findLatestVersion(tableId);
		List<TempTableData> latestDataList = tempdataRepository.findByVersionInfoId(latestVersion.getId()).get();
		for(TempTableData cell:latestDataList) {
			if(cell.getColumnNumber()==columnIndex) {
				tempdataRepository.deleteById(cell.getId());
			}
		}//delete logic
		
		for(TempTableData cell:latestDataList) {
			if(cell.getColumnNumber()>columnIndex) {
				int currentcolumnnumber = cell.getColumnNumber();
				cell.setColumnNumber(currentcolumnnumber-1);
				tempdataRepository.save(cell);
			}
		}//당기기
	}
	@Transactional
	public void deleteRow(long tableId, int rowIndex) {
		DataVersionControl latestVersion = dataVersionService.findLatestVersion(tableId);
		List<TempTableData> latestDataList = tempdataRepository.findByVersionInfoId(latestVersion.getId()).get();
		for(TempTableData cell:latestDataList) {
			if(cell.getRowNumber()==rowIndex) {
				tempdataRepository.deleteById(cell.getId());
			}
		}
		for(TempTableData cell:latestDataList) {
			if(cell.getRowNumber()>rowIndex) {
				int currentrownumber = cell.getRowNumber();
				cell.setRowNumber(currentrownumber-1);
				tempdataRepository.save(cell);
			}
		}
	}
	
}



 
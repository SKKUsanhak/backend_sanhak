package blueprint_table_ocr.webserver.azure.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import blueprint_table_ocr.webserver.azure.Dto.VersioningDto;
import blueprint_table_ocr.webserver.azure.Repository.DataVersionControlRepository;
import blueprint_table_ocr.webserver.azure.Repository.DocRepository;
import blueprint_table_ocr.webserver.azure.Repository.TempDataRepository;
import blueprint_table_ocr.webserver.datapart.DataVersionControl;
import blueprint_table_ocr.webserver.datapart.TableDoc;
import blueprint_table_ocr.webserver.datapart.TempTableData;
import jakarta.validation.Valid;

@Service
public class DataVersionService {
	private TempDataRepository tempdataRepository;
	private DocRepository docRepository;
	private DataVersionControlRepository dvcRepository;
	
	public DataVersionService(DocRepository docRepository, TempDataRepository tempdataRepository,DataVersionControlRepository dvcRepository) {
		this.docRepository = docRepository;
		this.tempdataRepository = tempdataRepository;
		this.dvcRepository = dvcRepository;
	}

	public DataVersionControl createNewVersion(long tableId, @Valid VersioningDto versioningDto) {//최종적으로는 생성된 현재의 버전 리턴?
		//최신 버전의 데이터 가져오기
		DataVersionControl latestVersion = findLatestVersion(tableId);
		List<TempTableData> latestDataList = tempdataRepository.findByVersionInfoId(latestVersion.getId()).get();
		// TODO 1.새로운 버전 객체 만들기
		DataVersionControl newVersion = new DataVersionControl();
		newVersion.setVersion(versioningDto.getVersion());
		newVersion.setNote(versioningDto.getNote());
		TableDoc table = docRepository.findById(tableId).get();
		newVersion.setTableInfo(table);
		newVersion.setUpdateTime(LocalDateTime.now());
		dvcRepository.save(newVersion);
		//2.그 객체를 참조하는 데이터들 그대로 만들기 
		 
		List<TempTableData> newDataList = new ArrayList<>();
		for(TempTableData cell : latestDataList) {//final로 옮기기
			TempTableData newData = new TempTableData();
			newData.setColumnName(cell.getColumnName());
			newData.setColumnNumber(cell.getColumnNumber());
			newData.setRowNumber(cell.getRowNumber());
			newData.setTableInfo(cell.getTableInfo());
			newData.setContents(cell.getContents());
			newData.setVersionInfo(newVersion);		
			newDataList.add(newData);
		}
		tempdataRepository.saveAll(newDataList);//동일한데 버전만 다른 데이터들이 생성
		 
		return newVersion;
	}
	
	public DataVersionControl findLatestVersion(long tableId) {//해당 테이블의 가장 최신 버전 객체를 찾음
		List<DataVersionControl> versionList = dvcRepository.findByTableInfoId(tableId).get();
		LocalDateTime latestTime = LocalDateTime.of(2004, 10, 20, 0, 0);
		DataVersionControl latestVersion = null;
		for(DataVersionControl version : versionList) {
			if(version.getUpdateTime().isAfter(latestTime)) {
				latestTime=version.getUpdateTime();
				latestVersion = version;
			}
		}
		return latestVersion;
	}

	
	

}

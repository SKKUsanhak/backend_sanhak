package blueprint_table_ocr.webserver.azure.service;

import java.util.List;

import org.springframework.stereotype.Service;

import blueprint_table_ocr.webserver.azure.Dto.BuildingDto;
import blueprint_table_ocr.webserver.azure.Repository.BuildingRepository;
import blueprint_table_ocr.webserver.azure.Repository.DataVersionControlRepository;
import blueprint_table_ocr.webserver.azure.Repository.DocRepository;
import blueprint_table_ocr.webserver.azure.Repository.FileRepository;
import blueprint_table_ocr.webserver.azure.Repository.TempDataRepository;
import blueprint_table_ocr.webserver.datapart.Building;
import blueprint_table_ocr.webserver.datapart.DataVersionControl;
import blueprint_table_ocr.webserver.datapart.OwnerFile;
import blueprint_table_ocr.webserver.datapart.TableDoc;
import blueprint_table_ocr.webserver.datapart.TempTableData;
import jakarta.validation.Valid;

@Service
public class BuildingService {
	private TempDataRepository tempdataRepository;
	private DocRepository docRepository;	
	private FileRepository fileRepository;
	private BuildingRepository buildingRepository;
	private DataVersionControlRepository dvcRepository;
	
	public BuildingService( DocRepository docRepository,FileRepository fileRepository, TempDataRepository tempdataRepository,BuildingRepository buildingRepository,DataVersionControlRepository dvcRepository) {
		
		this.docRepository = docRepository;
		this.fileRepository = fileRepository; 
		this.tempdataRepository = tempdataRepository;
		this.buildingRepository = buildingRepository;
		this.dvcRepository = dvcRepository;
	}
	
	//create building
	public Building createBuilding(@Valid BuildingDto buildingDto) {
		Building newBuilding = new Building();
		newBuilding.setBuildingName(buildingDto.getBuildingName());
		newBuilding.setAddress(buildingDto.getAddress());
		newBuilding.setTotalArea(buildingDto.getTotalArea());
		newBuilding.setBasementFloors(buildingDto.getBasementFloors());
		newBuilding.setGroundFloors(buildingDto.getGroundFloors());
		buildingRepository.save(newBuilding);
		return newBuilding;
	} 

	public List<Building> getBuildings() {
		List<Building> buildings = buildingRepository.findAll();
		return buildings;
	}

	public Building updateBuilding(Long buildingId, @Valid BuildingDto buildingDto) {
		Building editedBuilding = buildingRepository.findById(buildingId).get();
		editedBuilding.setBuildingName(buildingDto.getBuildingName());
		editedBuilding.setAddress(buildingDto.getAddress());
		editedBuilding.setTotalArea(buildingDto.getTotalArea());
		editedBuilding.setBasementFloors(buildingDto.getBasementFloors());
		editedBuilding.setGroundFloors(buildingDto.getGroundFloors());
		return buildingRepository.save(editedBuilding);
	}

	public void deleteBuilding(Long buildingId) {
		//1.파일 찾기 
		List<OwnerFile> fileList = fileRepository.findByBuildingInfoId(buildingId).get();
		for(OwnerFile file: fileList) {
			//2.파일마다 테이블 찾기
			List<TableDoc> tableList = docRepository.findByFileInfoId(file.getId()).get();
			for(TableDoc table: tableList) {
				//테이블 마다 참조관계 끊기
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
			
		}
		Building deletedBuilding = buildingRepository.findById(buildingId).get();
		buildingRepository.delete(deletedBuilding);
	}
	
	

}
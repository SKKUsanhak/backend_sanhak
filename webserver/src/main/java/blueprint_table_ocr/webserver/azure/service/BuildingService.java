package blueprint_table_ocr.webserver.azure.service;

import java.util.List;

import org.springframework.stereotype.Service;

import blueprint_table_ocr.webserver.azure.Dto.BuildingDto;
import blueprint_table_ocr.webserver.azure.Repository.BuildingRepository;
import blueprint_table_ocr.webserver.datapart.Building;
import jakarta.validation.Valid;

@Service
public class BuildingService {
	private BuildingRepository buildingRepository;
	BuildingService(BuildingRepository buildingRepository){
		this.buildingRepository=buildingRepository;
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
		Building deletedBuilding = buildingRepository.findById(buildingId).get();
		buildingRepository.delete(deletedBuilding);
	}
	
	

}

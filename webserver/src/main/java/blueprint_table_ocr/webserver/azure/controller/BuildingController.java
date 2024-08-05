package blueprint_table_ocr.webserver.azure.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import blueprint_table_ocr.webserver.azure.Dto.BuildingDto;
import blueprint_table_ocr.webserver.azure.service.BuildingService;
import blueprint_table_ocr.webserver.datapart.Building;
import jakarta.validation.Valid;

@RestController
public class BuildingController {
	   private BuildingService buildingService;
	   
	   public BuildingController(BuildingService buildingService) {
	      this.buildingService = buildingService;
	   }
	   
	   //create building
	   @PostMapping("/buildings")
	   public ResponseEntity<Building> createBuilding(@RequestBody @Valid BuildingDto buildingDto) {
	        Building newBuilding = buildingService.createBuilding(buildingDto);
	        return ResponseEntity.status(HttpStatus.CREATED).body(newBuilding);
	   }
	   
	   //read building list
	   @GetMapping("/buildings")
	    public ResponseEntity<List<Building>> getAllBuildings() {
	        List<Building> buildings = buildingService.getBuildings();
	        if (buildings.isEmpty()) {
		        return ResponseEntity.noContent().build(); // 테이블이 없을 경우 HTTP 204 No Content 반환
	        }
	        return ResponseEntity.ok(buildings);
	   }
	   
	   //update building info
	   @PatchMapping("/buildings/{buildingId}")
	   public ResponseEntity<Building> updateBuilding(@PathVariable Long buildingId, @RequestBody @Valid BuildingDto buildingDto) {
	        Building updatedBuilding = buildingService.updateBuilding(buildingId, buildingDto);
	        return ResponseEntity.ok(updatedBuilding);
	   }
	   
	   //delete building
	   @DeleteMapping("/buildings/{buildingId}")
	   public ResponseEntity<Void> deleteBuilding(@PathVariable Long buildingId) {
	        buildingService.deleteBuilding(buildingId);
	        return ResponseEntity.noContent().build();
	    }
}

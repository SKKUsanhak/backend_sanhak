package blueprint_table_ocr.webserver.azure.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import blueprint_table_ocr.webserver.datapart.Building;

public interface BuildingRepository extends JpaRepository<Building,Long> {

}

package blueprint_table_ocr.webserver.azure.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import blueprint_table_ocr.webserver.datapart.OwnerFile;

public interface FileRepository extends JpaRepository<OwnerFile,Long> {
	Optional<List<OwnerFile>> findByBuildingInfoId(long id);

}

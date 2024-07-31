package blueprint_table_ocr.webserver.azure.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import blueprint_table_ocr.webserver.datapart.OwnerFile;
import blueprint_table_ocr.webserver.datapart.TableDoc;

public interface FileRepository extends JpaRepository<OwnerFile,Long> {
	List<OwnerFile> findByBuildingInfoId(long id);

}

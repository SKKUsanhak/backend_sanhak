package blueprint_table_ocr.webserver.azure.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import blueprint_table_ocr.webserver.datapart.DataVersionControl;

public interface DataVersionControlRepository extends JpaRepository<DataVersionControl,Long>{
	Optional<List<DataVersionControl>> findByTableInfoId(long tableId);
}

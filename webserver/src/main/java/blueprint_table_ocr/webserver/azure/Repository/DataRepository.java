package blueprint_table_ocr.webserver.azure.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import blueprint_table_ocr.webserver.datapart.TableData;

@Repository
public interface DataRepository extends JpaRepository<TableData,Long>, CommonDataRepository<TableData>  {
	@Query("SELECT COUNT(e) FROM TableData e")
	long countAll();
	
	Optional<List<TableData>> findByTableInfoId(long tableId);

}  

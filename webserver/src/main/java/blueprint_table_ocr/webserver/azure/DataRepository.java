package blueprint_table_ocr.webserver.azure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DataRepository extends JpaRepository<TableData,Long> {
	List<TableData> findByrowNumber(Integer i);
	List<TableData> findByContentsContaining(String contents);
	@Query("SELECT COUNT(e) FROM TableData e")
	long countAll();

}

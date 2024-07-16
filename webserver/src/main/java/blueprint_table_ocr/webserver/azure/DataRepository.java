package blueprint_table_ocr.webserver.azure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataRepository extends JpaRepository<TableData,Long> {
	List<TableData> findByContents(String contents);
	List<TableData> findByrowNumber(Integer i);

}

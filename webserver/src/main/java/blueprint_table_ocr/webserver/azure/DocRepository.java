package blueprint_table_ocr.webserver.azure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocRepository extends JpaRepository<TableDoc,Long> {

	List<TableDoc> findByFileInfoId(long id);

}

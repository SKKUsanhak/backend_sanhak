package blueprint_table_ocr.webserver.azure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocRepository extends JpaRepository<TableDoc,Long> {

}

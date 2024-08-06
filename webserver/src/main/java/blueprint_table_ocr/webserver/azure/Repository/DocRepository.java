package blueprint_table_ocr.webserver.azure.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import blueprint_table_ocr.webserver.datapart.TableDoc;

@Repository
public interface DocRepository extends JpaRepository<TableDoc,Long> {

	Optional<List<TableDoc>> findByFileInfoId(long id);

}

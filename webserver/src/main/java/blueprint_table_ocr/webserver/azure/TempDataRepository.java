package blueprint_table_ocr.webserver.azure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TempDataRepository extends JpaRepository<TempTableData,Long> {

	Optional<List<TempTableData>> findByTableInfoId(long tableId);



}

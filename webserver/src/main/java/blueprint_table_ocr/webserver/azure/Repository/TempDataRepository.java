package blueprint_table_ocr.webserver.azure.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import blueprint_table_ocr.webserver.datapart.TempTableData;

@Repository
public interface TempDataRepository extends JpaRepository<TempTableData,Long> {

	Optional<List<TempTableData>> findByTableInfoId(long tableId);
	Optional<List<TempTableData>> findByVersionInfoId(long verionId);
	Optional<TempTableData> findByVersionInfoIdAndColumnNumberAndRowNumber(long verionId, int columNumber,int rowNumber);
}

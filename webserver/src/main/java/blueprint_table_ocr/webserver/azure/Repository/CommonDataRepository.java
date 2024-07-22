package blueprint_table_ocr.webserver.azure.Repository;

import java.util.List;
import java.util.Optional;

import blueprint_table_ocr.webserver.datapart.Data;

public interface CommonDataRepository <T extends Data> {
	 Optional<List<T>> findByTableInfoId(long tableId);
}

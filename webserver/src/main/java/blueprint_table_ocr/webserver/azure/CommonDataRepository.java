package blueprint_table_ocr.webserver.azure;

import java.util.List;
import java.util.Optional;

public interface CommonDataRepository <T extends Data> {
	 Optional<List<T>> findByTableInfoId(long tableId);
}

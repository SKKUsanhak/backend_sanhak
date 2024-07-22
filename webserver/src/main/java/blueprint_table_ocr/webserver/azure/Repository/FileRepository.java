package blueprint_table_ocr.webserver.azure.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import blueprint_table_ocr.webserver.datapart.OwnerFile;

public interface FileRepository extends JpaRepository<OwnerFile,Long> {

}

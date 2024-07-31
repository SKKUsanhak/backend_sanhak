package blueprint_table_ocr.webserver.azure.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import blueprint_table_ocr.webserver.security.UserData;

public interface UserRepository extends JpaRepository<UserData,Long>{
	Optional<UserData> findByUsername(String username);

}

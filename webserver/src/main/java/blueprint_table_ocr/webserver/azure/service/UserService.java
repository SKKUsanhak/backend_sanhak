package blueprint_table_ocr.webserver.azure.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import blueprint_table_ocr.webserver.azure.Repository.UserRepository;
import blueprint_table_ocr.webserver.datapart.UserData;
import blueprint_table_ocr.webserver.datapart.UserData.Role;
import blueprint_table_ocr.webserver.datapart.UserRegistrationDto;

@Service
public class UserService {
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	UserService(UserRepository userRepository,PasswordEncoder passwordEncoder){
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	 
	
	public void createUser(UserRegistrationDto userData) {
		UserData newUser = new UserData();
		newUser.setUsername(userData.getUsername());
		newUser.setEmail(userData.getEmail());
		newUser.setPassword(passwordEncoder.encode(userData.getPassword()));
		newUser.setRole(Role.ROLE_USER);
		
		userRepository.save(newUser);
		
	}

}

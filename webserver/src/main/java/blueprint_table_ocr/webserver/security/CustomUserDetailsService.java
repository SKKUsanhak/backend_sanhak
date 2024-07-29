package blueprint_table_ocr.webserver.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import blueprint_table_ocr.webserver.azure.Repository.UserRepository;
import blueprint_table_ocr.webserver.datapart.UserData;

@Service
public class CustomUserDetailsService implements UserDetailsService{
	
	private UserRepository userRepository;
	CustomUserDetailsService(UserRepository userRepository){
		this.userRepository = userRepository;
	}
	
	@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		 UserData user = userRepository.findByUsername(username)
	                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

	        return new CustomUserDetails(user);
	}

}

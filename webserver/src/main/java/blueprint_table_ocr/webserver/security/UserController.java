package blueprint_table_ocr.webserver.security;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class UserController {
	private UserService userService;
	private JwtTokenUtil jwtTokenUtil;
	private AuthenticationManager authenticationManager;
	UserController(UserService userService, AuthenticationManager authenticationManager,JwtTokenUtil jwtTokenUtil){
		this.userService = userService;
		this.authenticationManager = authenticationManager;
		this.jwtTokenUtil = jwtTokenUtil;
	}
	
	@PostMapping("/sign-up")
	public ResponseEntity<?> signUpUser(@Valid @RequestBody UserRegistrationDto userData, BindingResult bindingResult) {
	    if (bindingResult.hasErrors()) {
	        Map<String, String> errors = bindingResult.getFieldErrors().stream()
	            .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));//더 상세한 오류 메세지 반환하도록 개선하는코드
	        return ResponseEntity.badRequest().body(errors);
	    } 
	    else {
	        userService.createUser(userData);
	        return ResponseEntity.status(HttpStatus.CREATED).body("Sign up finished successfully.");
	    }
	}
	
	private static class LoginRequest {
		private String username;
		private String password;
		public String getUsername() {
			return username;
		}
		public String getPassword() {
			return password;
		}
	}
	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
		try {
			Authentication authentication = authenticationManager.authenticate(
		            new UsernamePasswordAuthenticationToken(
		                loginRequest.getUsername(), 
		                loginRequest.getPassword()
		            )
			);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = jwtTokenUtil.generateToken(authentication);
	        //UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			
			return ResponseEntity.ok(new JwtResponse(jwt));
		} 
		catch (Exception e) {
			 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("login failed: " + e.getMessage());  // 로그인 실패 시 메시지 반환
		}
		
	}
}
	



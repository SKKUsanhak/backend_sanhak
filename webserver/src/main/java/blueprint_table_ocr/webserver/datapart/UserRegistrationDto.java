package blueprint_table_ocr.webserver.datapart;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class UserRegistrationDto {//여기서 유효성 검증 실행
	
	@NotBlank
	@Size(min = 1, max = 20)
	private String username;
	
	@NotBlank
	@Size(min = 8, max = 20)
	private String password;
    
	@NotBlank
    @Email
    private String email;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
    
}

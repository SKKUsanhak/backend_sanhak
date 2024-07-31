package blueprint_table_ocr.webserver.security;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class UserData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;
/*
    @OneToMany(mappedBy =  "userInfo")
    @JsonIgnore
    @JsonManagedReference
    private List<OwnerFile> filelists;*/
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    public enum Role {
        ROLE_USER,
        ROLE_ADMIN
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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


	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

}

package blueprint_table_ocr.webserver.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {
	private final UserData user;
    public CustomUserDetails(UserData user) {
        this.user = user;
    }
   

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
    }//사용자의 역할을 단순히 String으로 반환하는 것이 아니라 GrantedAuthority 객체로 변환하여 반환해야해서 이렇게 함

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }
    
    
    @Override//이 아래는 필수로 구현해야하는 메서드들
    public boolean isAccountNonExpired() {
        return true; // 항상 계정이 만료되지 않았다고 설정
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 항상 계정이 잠겨있지 않다고 설정
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 항상 자격 증명이 만료되지 않았다고 설정
    }

    @Override
    public boolean isEnabled() {
        return true; // 항상 계정이 활성화되어 있다고 설정
    }

}

package blueprint_table_ocr.webserver.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenUtil, userDetailsService);
    }
    
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()  // CORS 설정 활성화
            .csrf().disable()  // CSRF 보호 비활성화
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll())  // 모든 요청에 대해 인증 없이 접근 가능한 엔드포인트 설정
            /*.authorizeHttpRequests(auth -> auth
                    .requestMatchers("/login", "/sign-up", "/h2-console/**").permitAll()  // 로그인, 회원가입, h2 콘솔은 허용
                    .anyRequest().authenticated())*/
            .formLogin().disable()  // 모든 사용자가 로그인 페이지에 접근할 수 있도록 설정
            .logout()
                .permitAll()  // 모든 사용자가 로그아웃할 수 있도록 설정
            .and()
            .headers().addHeaderWriter(new XFrameOptionsHeaderWriter( // 동일 출처 프레임만 허용 h2보이는 용도
                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN));  
        
        //http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
	
	@Bean
    PasswordEncoder passwordEncoder() {//인코더
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {//cors관현
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")//애플리케이션의 모든 경로를 대상
                        .allowedOrigins("http://localhost:3000")  // 프론트엔드 서버 주소
                        .allowedMethods("GET", "POST", "PUT", "DELETE","PATCH", "OPTIONS");
                        //.allowCredentials(true);
            }
        };
    } 
    
    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;//authenticatemanager관련
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
	
	
}

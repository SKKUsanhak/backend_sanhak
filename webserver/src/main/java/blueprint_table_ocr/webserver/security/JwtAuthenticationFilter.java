package blueprint_table_ocr.webserver.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtTokenUtil jwtTokenUtil;
    private UserDetailsService userDetailsService;
    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, UserDetailsService userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }//생성자 주입

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)//토큰 확인->유효성 검증->컨텍스트 설정
            throws ServletException, IOException{

        String header = request.getHeader("Authorization");//Authorization 헤더로부터 토큰 뽑기
        String token = null;
        String username = null;
        //Authorization 헤더가 있는 경우에만 JWT 토큰을 처리
        if (header != null && header.startsWith("Bearer ")) {//bearer Token 방식은 클라이언트가 서버로부터 발급받은 토큰을 Authorization 헤더에 Bearer 라는 접두어를 붙여서 요청에 포함시키는 방식
            token = header.substring(7);//토큰 추출
            try {
                username = jwtTokenUtil.extractUsername(token);//유저네임 추출
            } catch (Exception e) {
                logger.error("Cannot set user authentication: {}", e);
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);//이름 같은거 찾아오기
            if (jwtTokenUtil.validateToken(token, userDetails)) {//유효성 검증
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);//컨텍스트 설정 나중에 이걸로 사용자 권한등 확인
            }
        }
        chain.doFilter(request, response);
    }
}

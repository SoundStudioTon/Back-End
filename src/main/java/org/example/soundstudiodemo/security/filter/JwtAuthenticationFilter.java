package org.example.soundstudiodemo.security.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.soundstudiodemo.security.CustomUserDetails;
import org.example.soundstudiodemo.security.exception.JwtExceptionCode;
import org.example.soundstudiodemo.security.token.JwtAuthenticationToken;
import org.example.soundstudiodemo.security.util.JwtTokenizer;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenizer jwtTokenizer;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("필터인터널 실해애애앵");
        log.info("request : {}", request.getRequestURI());
        String token = getToken(request); //accessToken 얻어냄.
        log.info("token 뭘긴데 : {}", token);
        if(StringUtils.hasText(token)){
            try{
                getAuthentication(token);
            }catch (ExpiredJwtException e){
                request.setAttribute("exception", JwtExceptionCode.EXPIRED_TOKEN.getCode());
                log.error("Expired Token : {}",token,e);
                throw new BadCredentialsException("Expired token exception", e);
            }catch (UnsupportedJwtException e){
                request.setAttribute("exception", JwtExceptionCode.UNSUPPORTED_TOKEN.getCode());
                log.error("Unsupported Token: {}", token, e);
                throw new BadCredentialsException("Unsupported token exception", e);
            } catch (MalformedJwtException e) {
                request.setAttribute("exception", JwtExceptionCode.INVALID_TOKEN.getCode());
                log.error("Invalid Token: {}", token, e);
                throw new BadCredentialsException("Invalid token exception", e);
            } catch (IllegalArgumentException e) {
                request.setAttribute("exception", JwtExceptionCode.NOT_FOUND_TOKEN.getCode());
                log.error("Token not found: {}", token, e);
                throw new BadCredentialsException("Token not found exception", e);
            } catch (Exception e) {
                log.error("JWT Filter - Internal Error: {}", token, e);
                throw new BadCredentialsException("JWT filter internal exception", e);
            }
        }
        log.info("doFilter실행 차례");
        filterChain.doFilter(request, response);
    }

    private void getAuthentication(String token) {
        Claims claims = jwtTokenizer.parseAccessToken(token);
        log.info("claims 뭔뎅 : {}",claims);
        String email = claims.getSubject();
        Long userId = claims.get("userId", Long.class);
        String username = claims.get("username", String.class);
        List<GrantedAuthority> authorities = getGrantedAuthorities(claims);

        CustomUserDetails userDetails = new CustomUserDetails(username,"",authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

        Authentication authentication = new JwtAuthenticationToken(authorities,userDetails,null);
        log.info("authentication {} ",authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private List<GrantedAuthority> getGrantedAuthorities(Claims claims){
        List<String> roles = (List<String>)claims.get("roles");
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles){
            authorities.add(()->role);
        }
        log.info("authorities 나온다라라라 : {}", authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "))); //admin이 잘나온다.
        return authorities;
    }
    private String getToken(HttpServletRequest request) {
        log.info("request : {}", request.getRequestURI());

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            log.info("Header: {}", headerName);
        }


        String authorization = request.getHeader("Authorization");
        log.info("authorization : {}", authorization);
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
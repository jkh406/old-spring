package com.anbtech.webffice.global.jwt;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


/**
 * GenericFillterBean을 Extends에서 doFilter Override, 
 * 실제 실터링 로직은 doFilter 내부에 작성
 * Jwt의 인증정보를 SecurityContext에 저장하는 역할을 합니다.
 * JwtFilter의 doFilter 메소드에서 Reqeust가 들어올 때 SecurityContext에 Authentication 객체를 저장해 사용하게 됩니다.
 */
@Slf4j
public class JwtFilter extends GenericFilterBean{

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private JwtTokenProvider jwtTokenProvider;

    public JwtFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }


    /**
     * jwt 토큰의 인증정보를 SecurityContext에 저장하는 역할 수행
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
        throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String requestURI = httpServletRequest.getRequestURI();

        log.info("cookies requestURI ="+ requestURI);
        // 아래 URL로 접근시 토큰이 필요하지 않으므로 바로 접근시킴.
        if (requestURI.equals("/api/v1/login")
		|| requestURI.equals("/auth/login")
		|| requestURI.equals("/api/v1/logout")
        || requestURI.equals("/api/v1/token/getAccessToken")
        ){
        	log.info("cookies skip =");
            filterChain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = httpServletRequest.getCookies();
        String refreshToken = null;
        String useToken = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(HttpHeaders.SET_COOKIE)) {
                refreshToken = cookie.getValue();
                log.info("refreshToken ="+ cookie.getValue());
            }
        }
        
        if (requestURI.equals("/api/v1/token/getAccessToken")) {
            useToken = jwtTokenProvider.resolveToken(refreshToken);
            try {
                if (StringUtils.hasText(refreshToken) && jwtTokenProvider.validateToken(useToken)){
                    filterChain.doFilter(request, response);
                    return;
                }
            } catch (Exception e){
                log.error(e.getMessage(), e);
            }
        }
        else {
            useToken = jwtTokenProvider.resolveToken(httpServletRequest.getHeader("Authorization"));
        }

        try {
            log.info("useToken ="+ useToken);
            if (StringUtils.hasText(useToken) && jwtTokenProvider.validateToken(useToken)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(useToken);
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Security Contextx에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);

            }else {
                logger.debug("유효한 JWT 토큰이 없습니다, uri:{}", requestURI);
            }
            filterChain.doFilter(request, response);
            
        } catch (SecurityException | MalformedJwtException e) {
            logger.info("잘못된 JWT 서명입니다.");
            setErrorResponse(HttpStatus.UNAUTHORIZED,httpServletResponse, e);
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
            setErrorResponse(HttpStatus.UNAUTHORIZED,httpServletResponse, e);
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다.");
            setErrorResponse(HttpStatus.UNAUTHORIZED,httpServletResponse, e);
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다.");
            setErrorResponse(HttpStatus.UNAUTHORIZED,httpServletResponse, e);
        }
    }
        
    
    public void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable ex) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json; charset=UTF-8");
        JSONObject responseJson = new JSONObject();
        try {
            responseJson.put("HttpStatus", HttpStatus.UNAUTHORIZED);
            responseJson.put("message", ex.getMessage());
            responseJson.put("status", false);
            responseJson.put("statusCode", 401);
            responseJson.put("code", "Tk401");
            response.getWriter().print(responseJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
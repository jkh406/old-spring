package com.anbtech.webffice.global.controller;


import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anbtech.webffice.global.DTO.response.SingleDataResponse;
import com.anbtech.webffice.global.jwt.JwtTokenProvider;
import com.anbtech.webffice.global.service.ResponseService;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/token")
public class TokenController {
    
    @Autowired
    ResponseService responseService;
    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @PostMapping("/getAccessToken")
    public ResponseEntity<SingleDataResponse<String>> issueToken(@CookieValue(name = HttpHeaders.SET_COOKIE) Cookie refreshCookie) {
        try {
            log.info("토큰 start" + refreshCookie);
            String refreshToken = jwtTokenProvider.resolveToken(refreshCookie.getValue());

            if (isBlank(refreshToken) || !jwtTokenProvider.validateToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(responseService.getSingleDataResponse(false, "accessToken 발급 실패, refreshToken 유효기간 만료", "Tk402"));
            }

            Collection<? extends GrantedAuthority> accessTokenAuthorityCollection = jwtTokenProvider.getAuthentication(refreshToken).getAuthorities();
            List<String> accessTokenAuthorities = accessTokenAuthorityCollection.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            String accessTokenUserId = jwtTokenProvider.getUserId(refreshToken);
            String newAccessToken = "Bearer" + jwtTokenProvider.createAcessToken(accessTokenUserId, accessTokenAuthorities);

            log.debug("토큰 재발급 성공");
            return ResponseEntity.status(HttpStatus.OK).body(responseService.getSingleDataResponse(true, "accessToken 발급성공", newAccessToken));

        } catch (Exception e) {
            log.error("토큰 발급 중 에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}

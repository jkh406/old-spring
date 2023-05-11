package com.anbtech.webffice.domain.user;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anbtech.webffice.domain.user.dto.LoginDTO;
import com.anbtech.webffice.domain.user.dto.TokenDTO;
import com.anbtech.webffice.domain.user.dto.UserDTO;
import com.anbtech.webffice.domain.user.service.UserService;
import com.anbtech.webffice.global.DTO.response.BaseResponse;
import com.anbtech.webffice.global.DTO.response.ListDataResponse;
import com.anbtech.webffice.global.DTO.response.MapDataResponse;
import com.anbtech.webffice.global.DTO.response.SingleDataResponse;
import com.anbtech.webffice.global.exception.DuplicatedUsernameException;
import com.anbtech.webffice.global.exception.LoginFailedException;
import com.anbtech.webffice.global.exception.UserNotFoundException;
//import com.anbtech.webffice.global.jwt.TokenProvider;
import com.anbtech.webffice.global.service.ResponseService;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {
    
    @Autowired
    UserService userService;
    @Autowired
    ResponseService responseService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody UserDTO user) {
        try {
            userService.join(user);
            TokenDTO token = userService.tokenGenerator(user.getUser_ID());
            ResponseCookie responseCookie = ResponseCookie.from(HttpHeaders.SET_COOKIE, token.getRefreshToken())
                    .path("/")
                    .maxAge(14 * 24 * 60 * 60) // 14일
                    .httpOnly(true)
                    .build();
            SingleDataResponse<String> response = responseService.getSingleDataResponse(true, user.getUser_ID(),
                    token.getAccessToken());
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .body(response);
        } catch (DuplicatedUsernameException e) {
            BaseResponse response = responseService.getBaseResponse(false, e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDto) {
        log.info("login start !!!");
        try {
            UserDTO userDto = userService.login(loginDto);
            TokenDTO token = userService.tokenGenerator(userDto.getUser_ID());
            Map<String, Object> userDataMap = userDto.getUserDataMap();
            
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("Token", token.getAccessToken());
            data.put("User", userDataMap);
            
            ResponseCookie responseCookie = ResponseCookie.from(HttpHeaders.SET_COOKIE, token.getRefreshToken())
                    .path("/")
                    .maxAge(14 * 24 * 60 * 60) // 14일
                    .httpOnly(true)
                    .build();

            MapDataResponse<Object> response = responseService.getMapDataResponse(true, userDto.getUser_ID(), data);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, responseCookie.toString());
            return ResponseEntity.ok().headers(headers).body(response);

        } catch (LoginFailedException exception) {
            log.debug(exception.getMessage());
            BaseResponse response = responseService.getBaseResponse(false, exception.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping(value="/logout")
    public ResponseEntity<BaseResponse> logout(@CookieValue(value = HttpHeaders.SET_COOKIE) Cookie refreshCookie) {
        try {
            ResponseCookie responseCookie =
                    ResponseCookie.from(HttpHeaders.SET_COOKIE, "")
                            .path("/")
                            .httpOnly(true)
                            .secure(true)
                            .maxAge(0)
                            .build();
            BaseResponse response = responseService.getBaseResponse(true, "로그아웃 성공");
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .body(response);
        } catch (LoginFailedException exception) {
            log.debug(exception.getMessage());
            BaseResponse response = responseService.getBaseResponse(false, exception.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    

    /**
     * @param idDTO userId 전송을 위한 DTO
     * @return userId가 있다면 success값을 true, 없다면 false를 리턴.
     */
    @GetMapping("/users/{user_Id}")
    public ResponseEntity<BaseResponse> isHaveUser(@RequestParam String user_Id) {
        try {
            boolean isHaveUser = userService.haveUser(user_Id);
            String message = isHaveUser ? "회원가입된 유저입니다." : "회원가입 안된 유저입니다.";
            SingleDataResponse<Boolean> response = responseService.getSingleDataResponse(true, message, isHaveUser);
            return ResponseEntity.ok(response);

        } catch (UserNotFoundException exception) {
            log.debug(exception.getMessage());
            BaseResponse response = responseService.getBaseResponse(false, exception.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
}

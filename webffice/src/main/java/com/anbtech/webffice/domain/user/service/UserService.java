package com.anbtech.webffice.domain.user.service;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anbtech.webffice.domain.user.UserMapper;
import com.anbtech.webffice.domain.user.dto.LoginDTO;
import com.anbtech.webffice.domain.user.dto.TokenDTO;
import com.anbtech.webffice.domain.user.dto.UserDTO;
import com.anbtech.webffice.global.exception.DuplicatedUsernameException;
import com.anbtech.webffice.global.exception.LoginFailedException;
import com.anbtech.webffice.global.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService{
    // 암호화 위한 엔코더
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입 시 저장시간을 넣어줄 DateTime형
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
    Date time = new Date();
    String localTime = format.format(time);

    @Autowired
    UserMapper userMapper;


    /**
     * 유저 회원가입
     * @param userVo
     */
    @Transactional
    public boolean join(UserDTO user) {
        // 가입된 유저인지 확인
    	System.out.println("!!!");
        if (userMapper.findUserId(user.getUser_ID()).isPresent()) {
            System.out.println("!!!");
            throw new DuplicatedUsernameException("이미 가입된 유저입니다.");
        }
        
        // 가입 안했으면 아래 진행
        UserDTO userVo = UserDTO.builder()
        .user_ID(user.getUser_ID())
        .user_PW(passwordEncoder.encode(user.getUser_PW()))
        .user_role("ROLE_USER")
        .create_Date(localTime)
        .update_Date(localTime)
        .build();

        userMapper.join(userVo);
        
        return userMapper.findUserId(user.getUser_ID()).isPresent();
    }
    /**
     * 토큰 발급받는 메소드
     * @param loginDTO 로그인 하는 유저의 정보
     * @return result[0]: accessToken, result[1]: refreshToken
     */
    public UserDTO login (LoginDTO loginDTO) {

        UserDTO userDto = userMapper.findUser(loginDTO.getUser_Id())
                .orElseThrow(() -> new LoginFailedException("잘못된 아이디입니다"));
        
        if (!passwordEncoder.matches(loginDTO.getUser_Pw(), userDto.getPassword())) {
            throw new LoginFailedException("잘못된 비밀번호입니다");
        }

        return userDto;
    }

    /**
     * 유저가 db에 있는지 확인하는 함수
     * @param userid 유저의 아이디 입력
     * @return 유저가 있다면: true, 유저가 없다면: false
     */
    public boolean haveUser(String user_Id) {
        if (userMapper.findUserId(user_Id).isPresent()) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 유저의 아이디를 찾는 함수
     * @param userId 유저의 아이디 입력
     * @return 유저의 아이디가 없다면 에러를 뱉고, 있다면 userId리턴
     */
    public UserDTO findUserId(String user_Id) {
        return userMapper.findUserId(user_Id)
                .orElseThrow(() -> 
                    new DuplicatedUsernameException("유저 중복"));
    }
    
    /**
     * 권한이 있는 page가져오는 함수 
     */
    public List<UserDTO> pageList(String user_Id) {

        List<UserDTO> userDto = userMapper.pageList(user_Id);
        
        return  userDto;
    }
    
    /**
     * Token 생성
     */
    public TokenDTO tokenGenerator(String user_Id) {
        UserDTO userDto = userMapper.findUser(user_Id)
        .orElseThrow(() -> new LoginFailedException("잘못된 아이디입니다"));

        return TokenDTO.builder()
        .accessToken("Bearer" + jwtTokenProvider.createAcessToken(userDto.getUser_ID(), Collections.singletonList(userDto.getUser_role())))
        .refreshToken("Bearer" + jwtTokenProvider.createRefreshToken(userDto.getUser_ID(), Collections.singletonList(userDto.getUser_role())))
        .build();
    }

    
}

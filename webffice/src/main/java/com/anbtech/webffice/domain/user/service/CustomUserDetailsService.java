package com.anbtech.webffice.domain.user.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.anbtech.webffice.domain.user.UserMapper;
import com.anbtech.webffice.domain.user.dto.UserDTO;
import com.anbtech.webffice.global.exception.UserNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String user_Id) throws UsernameNotFoundException {
        return userMapper.findUser(user_Id)
                .map(user -> addAuthorities(user))
                .orElseThrow(() -> new UserNotFoundException(user_Id + ">ID를 찾을 수 없습니다."));
    }

    private UserDTO addAuthorities(UserDTO userDTO) {
        userDTO.setAuthorities(Arrays.asList(new SimpleGrantedAuthority(userDTO.getUser_role())));

        return userDTO;
    }
}
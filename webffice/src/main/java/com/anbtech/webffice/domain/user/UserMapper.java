package com.anbtech.webffice.domain.user;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.anbtech.webffice.domain.user.dto.UserDTO;

@Mapper
public interface UserMapper {
    void join(UserDTO userVo);  
    Optional<UserDTO> findUser(String user_Id);
    Optional<UserDTO> findUserId(String user_Id);
    List<UserDTO> pageList(String user_Id);
}

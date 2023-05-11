package com.anbtech.webffice.domain.admin;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {

    List<AdminVO> selectUser();
}

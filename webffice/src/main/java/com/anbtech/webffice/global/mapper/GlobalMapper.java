package com.anbtech.webffice.global.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GlobalMapper {
    int getNextIncrement(String tableName);
    
}

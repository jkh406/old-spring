package com.anbtech.webffice.global.service;

import com.anbtech.webffice.global.mapper.GlobalMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GlobalService {
    
    @Autowired
    GlobalMapper dbInfoMapper;

    public int getNextIncrement(String username) {
        return dbInfoMapper.getNextIncrement(username);
    }
}

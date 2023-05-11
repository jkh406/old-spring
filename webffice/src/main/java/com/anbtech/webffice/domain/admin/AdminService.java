package com.anbtech.webffice.domain.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    private AdminMapper adminMapper;

    public List<AdminVO> selectUser(){ 
    	return adminMapper.selectUser(); 
	}
}

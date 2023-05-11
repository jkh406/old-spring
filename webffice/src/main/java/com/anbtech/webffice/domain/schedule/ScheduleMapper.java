package com.anbtech.webffice.domain.schedule;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ScheduleMapper {
	
    List<ScheduleVO> selectSchedule(String id);
    void insertSchedule(ScheduleVO vo);
    void deleteSchedule(String id);
    
}
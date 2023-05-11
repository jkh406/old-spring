package com.anbtech.webffice.domain.schedule;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
	
    @Autowired
    private ScheduleService scheduleService;
    
    @GetMapping("/{id}")
    public List<ScheduleVO> selectSchedule(@PathVariable String id){ 
    	System.out.println("Scheduel List 출력 성공 " + id); 
    	return scheduleService.selectSchedule(id);
	}
    
    @PostMapping("/insert")
    public void insertSchedule(@RequestBody ScheduleVO schedule){
        scheduleService.insertSchedule(schedule);
    	System.out.println("Schedule DB 저장 성공 "); 
    }
    
    @DeleteMapping("/delete/{id}")
    public void deleteSchedule(@PathVariable String id) {
    	scheduleService.deleteSchedule(id);
    	System.out.println("Schedule DB 삭제 성공 "); 
    }
}
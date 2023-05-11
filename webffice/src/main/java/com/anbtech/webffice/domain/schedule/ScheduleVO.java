package com.anbtech.webffice.domain.schedule;
import lombok.Data;

@Data
public class ScheduleVO {
	
    public String board_id;
    public String board_title;
    public String start_date;
    public String end_date;
    public String register_id;
    public String chk_allday;

}


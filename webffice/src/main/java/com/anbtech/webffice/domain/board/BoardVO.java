package com.anbtech.webffice.domain.board;
import lombok.Data;

@Data
public class BoardVO {

    public String id;
    public String table_name;
    public String column_name;
    public String column_width;
    public String column_value;
    public String screen_name;
    public String data_type;
    public String dept_no;
    public String visible;
    public String sortable;
    public String column_render;
    
}

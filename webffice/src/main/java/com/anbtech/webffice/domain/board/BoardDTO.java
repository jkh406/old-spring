package com.anbtech.webffice.domain.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {
    private String writter;
    private String title;
    private String contents;
    private String board_no;
    private String createDate;
    private String updateDate;
    private String root_board_no;
}

package com.anbtech.webffice.domain.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.anbtech.webffice.domain.schedule.ScheduleVO;

@Mapper
public interface BoardMapper {
    List<BoardDTO> readBoardTitles(BoardRequestDTO boardRequestDTO);
    int getBoardListLimit(BoardRequestDTO boardRequestDTO);

    BoardDTO getBoard(int boardId);
    
    void createBoard(BoardDTO boardDTO);
    void updateBoard(BoardDTO boardDTO);
    void deleteBoard(String id);
    
    List<BoardDTO> getBoardComments(BoardRequestDTO boardRequestDTO);
    void createComments(BoardDTO boardDTO);
    
    List<BoardVO> getColumns(String id);
}

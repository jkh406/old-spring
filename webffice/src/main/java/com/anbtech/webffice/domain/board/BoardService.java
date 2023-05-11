package com.anbtech.webffice.domain.board;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.anbtech.webffice.domain.schedule.ScheduleVO;
import com.anbtech.webffice.global.jwt.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {
    
    @Autowired
    BoardMapper boardMapper;
    @Autowired
    JwtTokenProvider tokenProvider;
    
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
    Date time = new Date();
    String localTime = format.format(time);
    
    public List<BoardVO> getColumns(String id){ 
    	return boardMapper.getColumns(id); 
	}

    public boolean createBoard(BoardDTO boardDTO, String accessToken) {
        String token = tokenProvider.resolveToken(accessToken);
        String userId = tokenProvider.getUserId(token);

        BoardDTO board = BoardDTO.builder()
                        .writter(userId)
                        .contents(boardDTO.getContents())
                        .title(boardDTO.getTitle()) 
                        .createDate(localTime)
                        .updateDate(localTime).build();

        boardMapper.createBoard(board);
        return true;
    }
    
    public boolean createComments(BoardDTO boardDTO, String accessToken) {
        String token = tokenProvider.resolveToken(accessToken);
        String userId = tokenProvider.getUserId(token);

        BoardDTO board = BoardDTO.builder()
                        .writter(userId)
                        .root_board_no(boardDTO.getRoot_board_no())
                        .contents(boardDTO.getContents())
                        .title(boardDTO.getTitle()) 
                        .createDate(localTime)
                        .updateDate(localTime).build();

        boardMapper.createComments(board);
        return true;
    }

    public void deleteBoard(String id){
    	boardMapper.deleteBoard(id);
	}
    
    public boolean updateBoard(BoardDTO boardDTO, String accessToken){
        String token = tokenProvider.resolveToken(accessToken);
        String userId = tokenProvider.getUserId(token);
        
        BoardDTO board = BoardDTO.builder()
                .writter(userId)
                .board_no(boardDTO.getBoard_no())
                .contents(boardDTO.getContents())
                .title(boardDTO.getTitle()) 
                .createDate(localTime)
                .updateDate(localTime).build();
        
    	boardMapper.updateBoard(board);
        return true;
	}

    public List<BoardDTO> readBoardLists(BoardRequestDTO boardRequestDTO) {
        return boardMapper.readBoardTitles(boardRequestDTO);
    }
    
    public List<BoardDTO> getBoardComments(BoardRequestDTO boardRequestDTO) {
        return boardMapper.getBoardComments(boardRequestDTO);
    }

    public int getBoardListLimit(BoardRequestDTO boardRequestDTO) {
        return boardMapper.getBoardListLimit(boardRequestDTO);
    }

    public BoardDTO getBoard(int boardId) {
        return boardMapper.getBoard(boardId);
    }

    public String getUserIdFromToken(String accessToken) {
        String token = tokenProvider.resolveToken(accessToken);
        String userId = tokenProvider.getUserId(token);
        return userId;
    }
}

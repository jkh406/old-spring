package com.anbtech.webffice.domain.board;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.anbtech.webffice.global.DTO.response.MapDataResponse;
import com.anbtech.webffice.global.DTO.response.SingleDataResponse;
import com.anbtech.webffice.global.service.ResponseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {
    
    @Autowired
    ResponseService responseService;
    @Autowired
    BoardService boardService;
    
    @GetMapping("/{id}")
    public List<BoardVO> getColumns(@PathVariable String id){ 
    	return boardService.getColumns(id);
	}
    
    public final String uploadPath = "E:\\10.Source\\react\\public\\files";

    @PostMapping("/createBoard")
    public ResponseEntity<SingleDataResponse<Boolean>> createBoard(
//            @RequestBody BoardDTO boardDTO,
            @RequestHeader("Authorization") String accessToken,
            @RequestPart(value = "file",required = false) MultipartFile[] files,
            @RequestPart(value = "data") BoardDTO boardDTO) {
    	System.out.print("files ="+ files);
    	System.out.print("boardDTO ="+ boardDTO);

        boolean isEnd = boardService.createBoard(boardDTO, accessToken);
        SingleDataResponse<Boolean> response = responseService.getSingleDataResponse(true, "게시판 생성 성공", isEnd);

		Date nowDate = new Date();
		SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
		SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
		String strYear = yearFormat.format(nowDate);
		String strMonth = monthFormat.format(nowDate);

		String folderPath = strYear + "/" + strMonth;

		File folder = new File(uploadPath + "/" + folderPath);
		if (!folder.exists()) {
			try {
				folder.mkdirs(); //폴더 생성합니다. ("새폴더"만 생성)
				System.out.println("폴더가 생성완료.: " + uploadPath + "/" + folderPath);
			} catch (Exception e) {
				e.getStackTrace();
			}
		} else {
			System.out.println("폴더가 이미 존재합니다..");
		}
		
		try {
			for (MultipartFile mf : files) {
				String originFileName = mf.getOriginalFilename(); // 원본 파일 명
				String ext = originFileName.substring(originFileName.lastIndexOf(".") + 1);
	
				String fileIdSeq = new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis());
	
				String safeFile = fileIdSeq + "." + ext;
				
				File f1 = new File(uploadPath + "/" + folderPath + "/" + safeFile);
		    	System.out.print("files f1 ="+ f1);
				mf.transferTo(f1);  //파일 업로드	
			}
		} catch (Exception e) {
			e.getStackTrace();
		}

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/createComments")
    public ResponseEntity<SingleDataResponse<Boolean>> createComments(
            @RequestBody BoardDTO boardDTO,
            @RequestHeader("Authorization") String accessToken) {
    	System.out.print("boardDTO ="+ boardDTO.getRoot_board_no());

        boolean isEnd = boardService.createComments(boardDTO, accessToken);
        SingleDataResponse<Boolean> response = responseService.getSingleDataResponse(true, "댓글 생성 성공", isEnd);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/readBoardLists")
    public ResponseEntity<?> readBoardLists(@RequestBody BoardRequestDTO boardRequestDTO) {

        List<BoardDTO> boards = boardService.readBoardLists(boardRequestDTO);
        int limit = boardService.getBoardListLimit(boardRequestDTO);
        
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("Boards", boards);
        data.put("Limit", limit);
        
        MapDataResponse<Object> response = responseService.getMapDataResponse(true, "Success", data);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    @PostMapping("/readBoardComments")
    public ResponseEntity<?> readBoardComments(@RequestBody BoardRequestDTO boardRequestDTO) {

        List<BoardDTO> boards = boardService.getBoardComments(boardRequestDTO);

        SingleDataResponse<List<BoardDTO>> response = responseService.getSingleDataResponse(true, "Success", boards);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @DeleteMapping("/delete/{id}")
    public void deleteBoard(@PathVariable String id) {
    	boardService.deleteBoard(id);
    }
    
    @PostMapping("/updateBoard")
    public ResponseEntity<SingleDataResponse<Boolean>> updateBoard(
            @RequestBody BoardDTO boardDTO,
            @RequestHeader("Authorization") String accessToken) {

        boolean isEnd = boardService.updateBoard(boardDTO, accessToken);
        SingleDataResponse<Boolean> response = responseService.getSingleDataResponse(true, "게시판 수정 성공", isEnd);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

//    @PostMapping("/getBoardListLimit")
//    public ResponseEntity<SingleDataResponse<Integer>> getBoardListLimit() {
//        int limit = boardService.getBoardListLimit();
//        SingleDataResponse<Integer> response = responseService.getSingleDataResponse(
//                true,
//                "게시판 최대값 불러오기 성공",
//                limit);
//
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/getBoard")
    public ResponseEntity<SingleDataResponse<BoardDTO>> getBoard(@RequestParam int boardId) {
        BoardDTO boardDTO = boardService.getBoard(boardId);
        SingleDataResponse<BoardDTO> response = responseService.getSingleDataResponse(
                true,
                "게시판 불러오기 성공",
                boardDTO);

        return ResponseEntity.ok(response);
    }

}
package com.icia.board.controller;

import com.icia.board.dto.CommentDTO;
import com.icia.board.entity.BaseEntity;
import com.icia.board.entity.BoardEntity;
import com.icia.board.service.BoardService;
import com.icia.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/save")
    public ResponseEntity saveForm(@RequestBody CommentDTO commentDTO){

        System.out.println("commentDTO = " + commentDTO);
        try{
            commentService.save(commentDTO);
            // 댓글 작성 후에 댓글 목록을 끌어옴(끌어올땐 findAll을 호출하고 게시글에 달려있는 댓글목록을 가져와야도ㅣ니깐 boardId로 매개변수를 줌)
            List<CommentDTO> commentDTOList = commentService.findAll(commentDTO.getBoardId());
            return new ResponseEntity<>(commentDTOList, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }


}

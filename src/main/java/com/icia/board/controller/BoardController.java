package com.icia.board.controller;

import com.icia.board.dto.BoardDTO;
import com.icia.board.dto.CommentDTO;
import com.icia.board.service.BoardService;
import com.icia.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;
    private final CommentService commentService;

    @GetMapping("/save")
    public String saveForm() {
        return "boardPages/boardSave";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO) throws IOException {
        boardService.save(boardDTO);
        return "redirect:/board/";
    }

    @GetMapping("/")
    public String findAll(Model model) {
        List<BoardDTO> boardDTOList = boardService.findAll();
        model.addAttribute("boardList", boardDTOList);
        return "boardPages/boardList";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, @RequestParam("page") int page,
                           @RequestParam("type") String type,
                           @RequestParam("q") String q,
                           Model model) {
        System.out.println("id = " + id + ", page = " + page + ", type = " + type + ", q = " + q );
        boardService.updateHits(id);
        model.addAttribute("page",page);
        try {
            BoardDTO boardDTO = boardService.findById(id);
            model.addAttribute("board", boardDTO);
            model.addAttribute("type", type);
            model.addAttribute("q", q);
            List<CommentDTO> commentDTOList = commentService.findAll(id);
            if(commentDTOList.size()>0){
                //0보다 크면 댓글이있음
                model.addAttribute("commentList", commentDTOList);
            }else{
                model.addAttribute("commentList", null);
            }

            return "boardPages/boardDetail";
        } catch (NoSuchElementException e) {
            return "boardPages/boardNotFound";
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        boardService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("board", boardDTO);
        return "boardPages/boardUpdate";
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody BoardDTO boardDTO) {
        boardService.update(boardDTO);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    // /board?page=1(/board는 위에 묶여져있어서 겟매핑만 줘도된다
    @GetMapping
    public String paging(@PageableDefault(page = 1) Pageable pageable,
                         @RequestParam(value = "type", required = false, defaultValue = "") String type,
                         @RequestParam(value = "q", required = false, defaultValue = "") String q,
                         Model model) {
        System.out.println("page = " + pageable.getPageNumber());
        Page<BoardDTO> boardDTOS = boardService.paging(pageable, type, q);
        if (boardDTOS.getTotalElements() == 0) {
            model.addAttribute("boardList", null);
        } else {
            model.addAttribute("boardList", boardDTOS);
        }

        //시작페이지(startPage), 마지막페ㅣ지(endPage)값 계산
        //하단에 보여줄 페이지 갯수 3개
        //사용자가 1.2.3  보여주고있으면 하단에 보여지는 startPage는 1, endPage는 3
        int blockLimit = 3;
        int startPage = (((int) (Math.ceil((double) pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;
        // endPage 삼항 연산자 사용함
        int endPage = ((startPage + blockLimit - 1) < boardDTOS.getTotalPages()) ? startPage + blockLimit - 1 : boardDTOS.getTotalPages();
        //(startPage + blockLimit - 1)는 endPage 계산하는거 //boardDTOS.getTotalPages()는 max페이지 계산하는거
        //위에 내용은 if문으로 바꿔보기
       /* if((startPage + blockLimit - 1) < boardDTOS.getTotalPages()){
            endPage = startPage + blockLimit - 1;
        }else{
            endPage = boardDTOS.getTotalPages();
        }*/

        model.addAttribute("startPage",startPage);
        model.addAttribute("endPage",endPage);
        // 검색결과 페이징이 유지되려면 type,q값도 모델에 담아가야됨
        model.addAttribute("type",type);
        model.addAttribute("q",q);
        return "/boardPages/boardPaging";
    }


}
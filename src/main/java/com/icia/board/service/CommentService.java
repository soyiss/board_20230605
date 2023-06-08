package com.icia.board.service;

import com.icia.board.dto.CommentDTO;
import com.icia.board.entity.BoardEntity;
import com.icia.board.entity.CommentEntity;
import com.icia.board.repository.BoardRepository;
import com.icia.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;


    public Long save(CommentDTO commentDTO) {
        //댓글 저장할 땐 commentEntity로 변환해서 레퍼지로 넘겨줘야함

        //boardENtity를 얻어냄
        BoardEntity boardEntity = boardRepository.findById(commentDTO.getBoardId()).orElseThrow(() -> new NoSuchElementException());
        CommentEntity commentEntity = CommentEntity.toSaveEntity(commentDTO,boardEntity);
        return commentRepository.save(commentEntity).getId();
    }

    @Transactional
    public List<CommentDTO> findAll(Long boardId) {
        //1. BoardEntity에서 댓글 목록 가져오기
        BoardEntity boardEntity = boardRepository.findById(boardId).orElseThrow(() -> new NoSuchElementException());
//        //부모 엔티티에서 자식엔티티를 뽑아쓸 수있다(부모가 자식엔티티를 접근하는 상황에서는 @transactional을 붙여줘야지 동기화가된다)
//        List<CommentEntity> commentEntityList = boardEntity.getCommentEntityList();

        //2. CommentRepository에서 가져오기
        // select * from comment_table where board_id=?
        List<CommentEntity> commentEntityList = commentRepository.findByBoardEntity(boardEntity);

        List<CommentDTO> commentDTOList = new ArrayList<>();
        commentEntityList.forEach(comment -> {
            commentDTOList.add(CommentDTO.toDTO(comment));
        });
        return commentDTOList;
    }
}

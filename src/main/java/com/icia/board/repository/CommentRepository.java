package com.icia.board.repository;

import com.icia.board.entity.BoardEntity;
import com.icia.board.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity,Long> {

//extends JpaRepository<CommentEntity,Long>를 써서 스프링 빈으로 등록이된다 -->  의존성 주입이 가능해짐

    List<CommentEntity> findByBoardEntityOrderByIdDesc(BoardEntity boardEntity);


}

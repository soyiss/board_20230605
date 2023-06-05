package com.icia.board.repository;

import com.icia.board.entity.BoardEntity;
import com.icia.board.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity,Long> {
}

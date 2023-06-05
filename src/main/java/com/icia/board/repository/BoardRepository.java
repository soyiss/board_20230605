package com.icia.board.repository;

import com.icia.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface BoardRepository extends JpaRepository<BoardEntity,Long> {


    //조회수 증가터리 메소드
    // update board_table set board_hits=board_hits+1 where id=?이런 형태의 쿼리문이 필요하다.
    // jpql문법 (java presistence query language): 필요한 쿼리문을 직접 적용하고자 할 때 사용(jpa가 지원하지 않는 형식을 써야되는 경우 사용한다)
    @Modifying
    @Query(value = "update BoardEntity b set b.boardHits=b.boardHits+1 where b.id=:id")

    //위에 식이랑 같은 식
//    @Query(value = "update board_table set board_hits = board_hits+1 where id=:id", nativeQuery = true)
    void updateHits(@Param("id") long id);



}

package com.icia.board.repository;

import com.icia.board.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface BoardRepository extends JpaRepository<BoardEntity,Long> {


    //조회수 증가터리 메소드
    // update board_table set board_hits=board_hits+1 where id=?이런 형태의 쿼리문이 필요하다.
    // jpql문법 (java presistence query language): 필요한 쿼리문을 직접 적용하고자 할 때 사용(jpa가 지원하지 않는 형식을 써야되는 경우 사용한다)
    @Modifying
    @Query(value = "update BoardEntity b set b.boardHits=b.boardHits+1 where b.id=:id")

    //위에 식이랑 같은 식
//    @Query(value = "update board_table set board_hits = board_hits+1 where id=:id", nativeQuery = true)
    void updateHits(@Param("id") long id);

    // 제목으로 검색을 한다면
    // native query에서는(board_title에서 q를 포함하는 쿼리문만 조회)
    // select * from board_table where board_title like '%q%'
    //select는 메서드를 정의할때 findBy로 시작함(like는 Containing를 씀)
    List<BoardEntity> findByBoardTitleContaining(String q);


    // 작성자로 검색을 한다면
    List<BoardEntity> findByBoardWriterContaining(String q);


    // 작성자로 검색한 결과를 페이징 한다면
    //리턴 타입이 Page타입으로 바뀜,매개변수로 Pageable 객체를 넘겨줌
    Page<BoardEntity> findByBoardWriterContaining(String q, Pageable pageable);


    // 제목으로 검색한 결과를 id기준 내림차순 정렬 한다면(OrderByIdDesc를 붙여줌)
    List<BoardEntity> findByBoardTitleContainingOrderByIdDesc(String q);


    // 작성자 또는 제목에 검색어가 포함된 결과 조회
    // native query에서는
    // select * from board_table where board_title like '%q%' or board_writer like '%q%';
    // findBy BoardTitleContaining or BoardWriterContaining (or또는 and로 묶어준다)
    List<BoardEntity> findByBoardTitleContainingOrBoardWriterContainingOrderByIdDesc(String q1, String q2);





}

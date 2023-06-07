package com.icia.board.entity;

import com.icia.board.dto.BoardDTO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board_table")
@Getter @Setter
public class BoardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String boardWriter;

    @Column(length = 50, nullable = false)
    private String boardTitle;

    @Column(length = 20, nullable = false)
    private String boardPass;

    @Column(length = 500)
    private String boardContents;

    @Column
    private int boardHits;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    //updatable = false로 해줘서 수정할떄 영향을 받지 않음 (그래서 따로 안가져가도된다)


    //파일 여부를 판단하는 컬럼
    @Column
    private int fileAttached;


    // 참조관계 설정(부모테이블)
    // mappedBy = "boardEntity"는 매핑 // 보드파일엔티티에서 private BoardEntity boardEntity;을 가리킨다
    // @OneToMany는 DB에서 테이블간의 관계를 얘기함(1:N관계 등등)보드 엔티티는 1( @OneToMany) 보드파일엔티티는 N(@ManyToOne) 이다.
    @OneToMany(mappedBy = "boardEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BoardFileEntity> boardFileEntityList = new ArrayList<>(); // 참조관계를 위한 문법
    // 부모 하나에 자식은 여러개니까 BoardFileEntity는 List형태로 준다





    public static BoardEntity toSaveEntity(BoardDTO boardDTO) {
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setBoardWriter(boardDTO.getBoardWriter());
        boardEntity.setBoardPass(boardDTO.getBoardPass());
        boardEntity.setBoardTitle(boardDTO.getBoardTitle());
        boardEntity.setBoardContents(boardDTO.getBoardContents());
        boardEntity.setBoardHits(0);
        //파일이 없을떄 0
        boardEntity.setFileAttached(0);
        return boardEntity;
    }

    public static BoardEntity toUpdateEntity(BoardDTO boardDTO) {
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setId(boardDTO.getId());
        boardEntity.setBoardWriter(boardDTO.getBoardWriter());
        boardEntity.setBoardPass(boardDTO.getBoardPass());
        boardEntity.setBoardTitle(boardDTO.getBoardTitle());
        boardEntity.setBoardContents(boardDTO.getBoardContents());
        boardEntity.setBoardHits(boardDTO.getBoardHits());
        return boardEntity;
    }

    //파일이 첨부된 게시글이기 때문데 boardEntity.setFileAttached(1);
    public static BoardEntity toSaveEntityWithFile(BoardDTO boardDTO) {
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setBoardWriter(boardDTO.getBoardWriter());
        boardEntity.setBoardPass(boardDTO.getBoardPass());
        boardEntity.setBoardTitle(boardDTO.getBoardTitle());
        boardEntity.setBoardContents(boardDTO.getBoardContents());
        boardEntity.setBoardHits(0);
        //파일이 있을때 1
        boardEntity.setFileAttached(1);
        return boardEntity;

    }
}
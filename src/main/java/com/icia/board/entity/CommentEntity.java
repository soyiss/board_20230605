package com.icia.board.entity;

import com.icia.board.dto.CommentDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "comment_table")
public class CommentEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length=20,nullable = false)
    private String commentWriter;
    @Column(length=200,nullable = false)
    private String commentContents;

    //자식테이블(참조관계)
    @ManyToOne // 자식이 여러개니까 (N관계) ManyToOne를 써줌
    @JoinColumn(name="board_id") //실제 DB에서 생성되는 참조 컬럼의 이름 // board_id
    private BoardEntity boardEntity; //여기는 반드시 부모엔티티 타입이 온다 !!!!(!_!중요!_!)


    //CommentDTO -> CommentEntity로 변환하는 메서드
    public static CommentEntity toSaveEntity(CommentDTO commentDTO ,BoardEntity boardEntity){
        CommentEntity commentEntity  =  new CommentEntity();
        commentEntity.setCommentWriter(commentDTO.getCommentWriter());
        commentEntity.setCommentContents(commentDTO.getCommentContents());
        commentEntity.setBoardEntity(boardEntity);
        return commentEntity;
    }



}

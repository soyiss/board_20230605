package com.icia.board.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "comment_table")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length=20,nullable = false)
    private String commentWriter;
    @Column(length=200,nullable = false)
    private String commentContents;
    @Column
    private Long boardId;

}

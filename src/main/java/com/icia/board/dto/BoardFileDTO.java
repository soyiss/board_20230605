package com.icia.board.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardFileDTO {

    private Long id;
    private String originalFileName;
    private String storedFileName;
    private Long BoardId;

}

package com.icia.board.service;

import com.icia.board.dto.BoardDTO;
import com.icia.board.entity.BoardEntity;
import com.icia.board.entity.BoardFileEntity;
import com.icia.board.repository.BoardFileRepository;
import com.icia.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;

    public Long save(BoardDTO boardDTO) throws IOException {

        if(boardDTO.getBoardFile().isEmpty()){
            //파일없음
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
            return boardRepository.save(boardEntity).getId();
        }else{
            //파일있음
            // 1. Board 테이블에 데이터를 먼저 저장
            // boardEntity는 id가 없음(insert전)
            //savedEntity는 id가 있음(insert후)
            BoardEntity boardEntity = BoardEntity.toSaveEntityWithFile(boardDTO);

            //자식데이터를 저장할때 부모의 객체를 받아야되서 여기다 미리 받아놈
            BoardEntity savedEntity = boardRepository.save(boardEntity);
            // 2. 파일이름 꺼내고, 저장용이름 만들고 , 파일로컬에 저장
            String originalFileName = boardDTO.getBoardFile().getOriginalFilename();
            String storedFileName = System.currentTimeMillis() + "-" + originalFileName;
            String savePath = "D:\\springboot_img\\"+storedFileName;
            boardDTO.getBoardFile().transferTo(new File(savePath));
            // 3. BoardFileEntity 로 변환 후 board_file_table에 저장
            //자식 데이터를 저장할 때 반드시 부모의 id가 아닌 부모의 Entity 객체가 전달되어야 한다(savedEntity가 부모의 entity값이다)
            BoardFileEntity boardFileEntity = BoardFileEntity.toSaveBoardFileEntity(savedEntity, originalFileName, storedFileName);
            boardFileRepository.save(boardFileEntity);
            return savedEntity.getId();
        }

    }

    @Transactional
    public List<BoardDTO> findAll() {
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDTO> boardDTOList = new ArrayList<>();
//        for (BoardEntity boardEntity: boardEntityList) {
//            boardDTOList.add(BoardDTO.toDTO(boardEntity));
//        }

        //위에 포문과 같은 의미
        boardEntityList.forEach(boardEntity -> {
            boardDTOList.add(BoardDTO.toDTO(boardEntity));
        });
        return boardDTOList;
    }


    @Transactional //repository에서 jquery 어노테이션을 사용하려면 그걸 호출하는 메서드에 transactional어노테이션을 써줘야한다
    public void updateHits(Long id) {

        boardRepository.updateHits(id);


    }
    @Transactional
    public BoardDTO findById(Long id) {
        BoardEntity boardEntity = boardRepository.findById(id).orElseThrow(() -> new NoSuchElementException());
        return BoardDTO.toDTO(boardEntity);
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    public void update(BoardDTO boardDTO) {
        BoardEntity boardEntity = BoardEntity.toUpdateEntity(boardDTO);
        boardRepository.save(boardEntity);
    }
}

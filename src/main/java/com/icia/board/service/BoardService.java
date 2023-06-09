package com.icia.board.service;

import com.icia.board.dto.BoardDTO;
import com.icia.board.entity.BoardEntity;
import com.icia.board.entity.BoardFileEntity;
import com.icia.board.repository.BoardFileRepository;
import com.icia.board.repository.BoardRepository;
import com.icia.board.util.UtilClass;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

        //파일이 여러갠데 첫번째칸에 파일이없으면 다 없다라고 판단하고 있으면 있다라고 판단을 하겠다
        //boardDTO.getBoardFile() == null 이거는 테스트 조건을 쓰다보니 필요해서 써야됌 (테스트 코드 안하면 안써도됌)
        if(boardDTO.getBoardFile() == null || boardDTO.getBoardFile().get(0).isEmpty()){
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
            for (MultipartFile boardFile: boardDTO.getBoardFile()) {
                String originalFileName = boardFile.getOriginalFilename();
                String storedFileName = System.currentTimeMillis() + "_" + originalFileName;
                String savePath = "D:\\springboot_img\\" + storedFileName;
                boardFile.transferTo(new File(savePath));
                // 3. BoardFileEntity로 변환 후 board_file_table에 저장
                // 자식 데이터를 저장할 때 반드시 부모의 id가 아닌 부모의 Entity 객체가 전달돼야 함.
                //자식 데이터를 저장할 때 반드시 부모의 id가 아닌 부모의 Entity 객체가 전달되어야 한다(savedEntity가 부모의 entity값이다)
                BoardFileEntity boardFileEntity =
                        BoardFileEntity.toSaveBoardFileEntity(savedEntity, originalFileName, storedFileName);
                boardFileRepository.save(boardFileEntity);
            }

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

    // 사용자가 요청하는 페이지가 있으면 JPA한테 요청할땐 무조건 1을 뺴줘서 요청해야한다
    public Page<BoardDTO> paging(Pageable pageable, String type, String q) {
        int page = pageable.getPageNumber()-1;
        int pageLimit = 5;
        Page<BoardEntity> boardEntities = null;
        if (type.equals("title")) {
            // 제목으로 검색요청이 왔다면
            boardEntities = boardRepository.findByBoardTitleContaining(q, PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));
        }else if(type.equals("writer")){
            // 작성자로 검색용청이 왔다면
            boardEntities = boardRepository.findByBoardWriterContaining(q, PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));
        }else {
            boardEntities = boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));
        }
        //일반 메서드로 검색요청이 왔다면
        //page는 몇페이지를 볼거냐
        //pageLimit는 몇개 글씩 볼거냐
        //Sort.by(Sort.Direction.DESC,"id")는 id를 기준으로 내림차순 정렬 하겠다
        Page<BoardDTO> boardDTOS = boardEntities.map(boardEntity -> BoardDTO.builder()
                .id(boardEntity.getId())
                .boardTitle(boardEntity.getBoardTitle())
                .boardWriter(boardEntity.getBoardWriter())
                .createdAt(UtilClass.dateFormat(boardEntity.getCreatedAt()))
                .boardHits(boardEntity.getBoardHits())
                .build());
        return boardDTOS;
    }
}

package com.kojos.board.service;

import com.kojos.board.domain.entity.BoardEntity;
import com.kojos.board.domain.repository.BoardRepository;
import com.kojos.board.dto.BoardDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class BoardSerive {

    private BoardRepository boardRepository;

    private static final int BLOCK_PAGE_NUM_COUNT = 5;
    private static final int PAGE_POST_COUNT = 4;


    public List<BoardDto> getBoardlist(Integer pageNum){
        Page<BoardEntity> page = boardRepository.findAll(PageRequest.of(pageNum -1 , PAGE_POST_COUNT, Sort.by(Sort.Direction.ASC, "createdDate")));

//        List<BoardEntity> boardEntities = boardRepository.findAll();
        List<BoardEntity> boardEntities = page.getContent();
        List<BoardDto> boardDtoList = new ArrayList<>();

        for (BoardEntity boardEntity: boardEntities) {

//            BoardDto boardDto = BoardDto.builder()
//                    .id(boardEntity.getId())
//                    .title(boardEntity.getTitle())
//                    .content(boardEntity.getContent())
//                    .writer(boardEntity.getWriter())
//                    .createdDate(boardEntity.getCreatedDate())
//                    .build();
//            boardDtoList.add(boardDto);
            boardDtoList.add(this.convertEntityToDto(boardEntity));
        }
        return boardDtoList;
    }

    public Long getBoardCount(){
        return boardRepository.count();
    }

    public Integer[] getPageList(Integer curPageNum) {
        Integer[] pageList = new Integer[BLOCK_PAGE_NUM_COUNT];

        Double postsTotalCount = Double.valueOf(this.getBoardCount());

        Integer totalLastPageNum = (int)(Math.ceil((postsTotalCount/PAGE_POST_COUNT)));

        Integer blockLastPageNum = (totalLastPageNum > curPageNum + BLOCK_PAGE_NUM_COUNT)
                ? curPageNum + BLOCK_PAGE_NUM_COUNT
                : totalLastPageNum;

        curPageNum = (curPageNum <=3)? 1 : curPageNum -2;

        for(int val = curPageNum, idx=0; val<= blockLastPageNum; val++, idx++){
            pageList[idx] = val;
        }
        return pageList;
    }

    public BoardDto getPost(Long id){
        Optional<BoardEntity> boardEntityWrapper = boardRepository.findById(id);
        BoardEntity boardEntity = boardEntityWrapper.get();
        BoardDto boardDto = BoardDto.builder()
                .id(boardEntity.getId())
                .title(boardEntity.getTitle())
                .content(boardEntity.getContent())
                .writer(boardEntity.getWriter())
                .createdDate(boardEntity.getCreatedDate())
                .build();
        return boardDto;
    }

    @Transactional
    public Long savePost(BoardDto boardDto) {
        return boardRepository.save(boardDto.toEntity()).getId();
    }

    @Transactional
    public void deletePost(Long id) {
        boardRepository.deleteById(id);
    }

    public List<BoardDto> searchPosts(String keyword){
        List<BoardEntity> boardEntities = boardRepository.findByTitleContaining(keyword);
        List<BoardDto> boardDtoList = new ArrayList<>();

        if(boardEntities.isEmpty()) return  boardDtoList;

        for(BoardEntity boardEntity : boardEntities){
            boardDtoList.add(this.convertEntityToDto(boardEntity));
        }

        return boardDtoList;
    }

    private BoardDto convertEntityToDto(BoardEntity boardEntity){
        return BoardDto.builder()
                .id(boardEntity.getId())
                .title(boardEntity.getTitle())
                .content(boardEntity.getContent())
                .writer(boardEntity.getWriter())
                .createdDate(boardEntity.getCreatedDate())
                .build();
    }

}

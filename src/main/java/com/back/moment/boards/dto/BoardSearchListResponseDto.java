package com.back.moment.boards.dto;

import com.back.moment.boards.entity.Board;
import com.back.moment.global.dto.TagResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class BoardSearchListResponseDto {
    private Long boardId;
    private  String title;
    private  String role;
    private  String nickName;
    private  String boardImgUrl;
    private LocalDateTime createdTime;
    private List<TagResponseDto> tag_boardList;

    public BoardSearchListResponseDto(Board board) {
        this.boardId = board.getId();
        this.title = board.getTitle();
        this.role = board.getUsers().getRole();
        this.nickName = board.getUsers().getNickName();
        this.boardImgUrl = board.getBoardImgUrl();
        this.createdTime = board.getCreatedAt().plusHours(9L);
        this.tag_boardList = board.getTagListWithWell();
    }
}

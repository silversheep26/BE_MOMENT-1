package com.back.moment.photos.dto;

import com.back.moment.photos.entity.Photo;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OnlyPhotoResponseDto {
    private String photoUrl;
    private int loveCnt;

    public OnlyPhotoResponseDto(Photo photo){
        this.photoUrl = photo.getImagUrl();
        this.loveCnt = photo.getLoveCnt();
    }
}

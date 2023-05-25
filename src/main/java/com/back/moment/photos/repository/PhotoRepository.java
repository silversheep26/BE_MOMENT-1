package com.back.moment.photos.repository;

import com.back.moment.photos.dto.PhotoFeedResponseDto;
import com.back.moment.photos.dto.OnlyPhotoResponseDto;
import com.back.moment.photos.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    @Query("select new com.back.moment.photos.dto.PhotoFeedResponseDto(p) from Photo p")
    List<PhotoFeedResponseDto> getAllPhoto();

    @Query("select new com.back.moment.photos.dto.OnlyPhotoResponseDto(p) from Photo p")
    List<OnlyPhotoResponseDto> getAllOnlyPhoto();

    @Query("select new com.back.moment.photos.dto.OnlyPhotoResponseDto(p) from Photo p where p.users.id = :hostId")
    List<OnlyPhotoResponseDto> getAllOnlyPhotoByHostId(@Param("hostId") Long hostId);


}
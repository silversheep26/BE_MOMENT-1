package com.back.moment.feed.service;

import com.back.moment.exception.ApiException;
import com.back.moment.exception.ExceptionEnum;
import com.back.moment.feed.dto.FeedDetailResponseDto;
import com.back.moment.feed.dto.FeedListResponseDto;
import com.back.moment.love.entity.Love;
import com.back.moment.love.repository.LoveRepository;
import com.back.moment.photos.dto.PhotoFeedResponseDto;
import com.back.moment.photos.entity.Photo;
import com.back.moment.photos.repository.PhotoRepository;
import com.back.moment.recommend.entity.Recommend;
import com.back.moment.recommend.repository.RecommendRepository;
import com.back.moment.s3.S3Uploader;
import com.back.moment.users.entity.Users;
import com.back.moment.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final S3Uploader s3Uploader;
    private final PhotoRepository photoRepository;
    private final LoveRepository loveRepository;
    private final RecommendRepository recommendRepository;
    private final UsersRepository usersRepository;

    public ResponseEntity<Void> uploadImages(List<MultipartFile> imageList, Users users) throws IOException {
        for(MultipartFile image : imageList){
            String imageUrl = s3Uploader.upload(image);
            Photo photo = new Photo(users, imageUrl);
            photoRepository.save(photo);
        }
        return ResponseEntity.ok(null);
    }

    @Transactional
    public ResponseEntity<String> lovePhoto(Long photoId, Users users) {
        Photo photo = photoRepository.findById(photoId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_PHOTO)
        );

        Love love = new Love(users, photo);
        Love existLove = loveRepository.findExistLove(photoId, users.getId());

        String message;

        if(existLove != null){
            loveRepository.delete(existLove);
            message = "좋아요 취소";
        }else {
            loveRepository.save(love);
            message = "좋아요 등록";
        }
        int loveCnt = loveRepository.findCntByPhotoId(photoId);
        photo.setLoveCnt(loveCnt);
        photoRepository.save(photo);

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> recommendUser(String nickName, Users users){
        Users recommendedUser = usersRepository.findByNickName(nickName).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_USER)
        );

        Recommend recommend = new Recommend(users, recommendedUser);
        Recommend existRecommend = recommendRepository.existRecommend(users.getNickName(), nickName);

        String message;

        if(existRecommend != null){
            recommendRepository.delete(existRecommend);
            message = "추천 취소";
        } else{
            recommendRepository.save(recommend);
            message = "추천 등록";
        }
        int recommendCnt = recommendRepository.countByRecommendedId(recommendedUser.getId());
        recommendedUser.setRecommendCnt(recommendCnt);
        usersRepository.save(recommendedUser);

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<FeedListResponseDto> getAllFeeds(Users users){
        List<PhotoFeedResponseDto> photoList = photoRepository.getAllPhoto();

        List<PhotoFeedResponseDto> topThreePhotos = photoList.stream()
                .sorted(Comparator.comparingInt(PhotoFeedResponseDto::getLoveCnt).reversed())
                .limit(3)
                .toList();

        photoList.removeAll(topThreePhotos);
        Collections.shuffle(photoList);

        List<PhotoFeedResponseDto> responsePhotoList = new ArrayList<>(topThreePhotos);
        responsePhotoList.addAll(photoList);

        return new ResponseEntity<>(new FeedListResponseDto(responsePhotoList), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<FeedDetailResponseDto> getFeed(Long photoId, Users users){
        Photo photo = photoRepository.findById(photoId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_PHOTO)
        );

        FeedDetailResponseDto feedDetailResponseDto = new FeedDetailResponseDto(photo.getImagUrl(),
                                                                                photo.getUsers().getProfileImg(),
                                                                                photo.getUsers().getNickName());

        if(loveRepository.existsByIdAndUsersId(photoId, users.getId())) feedDetailResponseDto.setCheckLove(true);
        if(recommendRepository.existsByRecommendedIdAndRecommenderId(photo.getUsers().getId(), users.getId())) feedDetailResponseDto.setCheckRecommend(true);

        return new ResponseEntity<>(feedDetailResponseDto, HttpStatus.OK);
    }


}
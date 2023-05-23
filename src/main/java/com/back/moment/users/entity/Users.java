package com.back.moment.users.entity;

import com.back.moment.boards.entity.Board;
import com.back.moment.love.entity.Love;
import com.back.moment.recommend.entity.Recommend;
import com.back.moment.users.dto.SignupRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;  // 이메일

    @Column(nullable = false)
    private String nickName;  // 닉네임

    @Column(nullable = false)
    private String password;  // 비밀번호

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private SexEnum sex;  // 성별

    @Column
    private String profileImg;  // 프로필 사진

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private RoleEnum role;  // 모델 또는 작가

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Love> loveList = new ArrayList<>();  //좋아요

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Board> boardList = new ArrayList<>();

    @OneToMany(mappedBy = "recommender", cascade = CascadeType.ALL)
    private List<Recommend> recommenderList = new ArrayList<>();  // 추천 하는 사람 -> 추천 두번 누르면 취소 돼야 하지 않을까?

    @OneToMany(mappedBy = "recommended", cascade = CascadeType.ALL)
    private List<Recommend> recommendedList = new ArrayList<>();  // 추천 받은 사람

    private Users(String email, String nickName, String password, SexEnum sex, String profileImg, RoleEnum role){
        this.email = email;
        this.nickName = nickName;
        this.password = password;
        this.sex = sex;
        this.profileImg = profileImg;
        this.role = role;
    }

    public void saveUsers(SignupRequestDto requestDto, SexEnum sex, RoleEnum role) {
        this.email = requestDto.getEmail();
        this.password = requestDto.getPassword();
        this.nickName = requestDto.getNickName();
        this.sex = sex;
        this.role = role;
    }

}

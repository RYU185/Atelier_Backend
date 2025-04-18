package com.dw.artgallery.DTO;

import com.dw.artgallery.enums.Gender;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserUpdateDTO {
    private String currentPassword;
    private String password;      // 선택
    private String realName;      // 수정
    private String phone;         // 수정
    private String email;         // 수정
    private String address;       // 수정
    private Gender gender;        // 수정
    private String nickName;
}
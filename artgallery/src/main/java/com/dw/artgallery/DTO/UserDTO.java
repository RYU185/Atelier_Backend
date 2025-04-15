package com.dw.artgallery.DTO;

import com.dw.artgallery.enums.Gender;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO {
    // 회원가입
    private String userId;
    private String password;
    private String nickName;
    private String realName;
    private String email;
    private LocalDate birthday;
    private String address;
    private String phone;
    private LocalDate enrolmentDate;
    private double point;
    private Gender gender;
}

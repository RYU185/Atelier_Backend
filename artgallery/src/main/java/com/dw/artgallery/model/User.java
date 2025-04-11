package com.dw.artgallery.model;

import com.dw.artgallery.DTO.UserDTO;
import com.dw.artgallery.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name="user")
public class User implements UserDetails {

    @Id
    @Column(name="user_id",nullable = false, unique = true)
    private String userId;

    @Column(name="password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickName;

    @Column(name = "real_name", nullable = false)
    private String realName;

    @Column(name="email", nullable = false, unique = true)
    private String email;

    @Column(name="birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "address")
    private String address;

    @Column(name = "enrolment_date")
    private LocalDate enrolmentDate = LocalDate.now(); // 기본값 설정

    @Column(name = "point")
    private double point ; // 기본 포인트 설정

    @Column(name="gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "user_authority")
    private Authority authority;

    @Column(name = "is_artist", nullable = false)
    private boolean isArtist = false;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Artist artistProfile;


    public User(String userId, String encodedPassword, String nickName, String realName, String email, LocalDate birthday, String address, LocalDate enrolmentDate, double point, Gender gender, Authority authority) {
    }

    public UserDTO toDTO() {
        return new UserDTO(
                this.userId,
                null,
                this.nickName,
                this.realName,
                this.email,
                this.birthday,
                this.address,
                this.enrolmentDate,
                this.point,
                this.gender
        );
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(authority.getAuthorityName()));
    }


    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}







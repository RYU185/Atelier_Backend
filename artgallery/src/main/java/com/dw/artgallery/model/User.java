package com.dw.artgallery.model;

import com.dw.artgallery.DTO.UserDTO;
import com.dw.artgallery.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "user")
public class User implements UserDetails {

    @Id
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickName;

    @Column(name = "real_name", nullable = false)
    private String realName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "address")
    private String address;

    @Column(name = "enrolment_date")
    private LocalDate enrolmentDate;

    @Column(name = "point")
    private double point;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "user_authority")
    private Authority authority;

    @Column(name = "is_artist", nullable = false)
    private boolean isArtist = false;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Artist artistProfile;

    @OneToMany(mappedBy = "user")
    private List<Purchase> purchases;

    @OneToMany(mappedBy = "user")
    private List<RealDrawing> drawings;

    @PrePersist
    protected void onCreate() {
        if (enrolmentDate == null) {
            enrolmentDate = LocalDate.now();
        }
        if (point == 0.0) {
            point = 0.0;
        }
    }

    public boolean isAdmin() {
        return this.authority != null && "ROLE_ADMIN".equalsIgnoreCase(this.authority.getAuthorityName());
    }

    // 실제 동작하는 생성자 추가
    public User(String userId, String encodedPassword, String nickName, String realName,
                String email, LocalDate birthday, String address,
                LocalDate enrolmentDate, double point, Gender gender, Authority authority, String phone) {
        this.userId = userId;
        this.password = encodedPassword;
        this.nickName = nickName;
        this.realName = realName;
        this.email = email;
        this.birthday = birthday;
        this.address = address;
        this.enrolmentDate = enrolmentDate != null ? enrolmentDate : LocalDate.now();
        this.point = point;
        this.gender = gender;
        this.authority = authority;
        this.phone = phone;
    }

    public UserDTO toDTO() {
        return new UserDTO(
                this.userId,
                null, // 비밀번호는 보안상 null 처리
                this.nickName,
                this.realName,
                this.email,
                this.birthday,
                this.address,
                this.phone, // 🔹 추가된 연락처
                this.enrolmentDate,
                this.point,
                this.gender
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authority != null) {
            return Collections.singletonList(new SimpleGrantedAuthority(authority.getAuthorityName()));
        }
        return Collections.emptyList();
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

package com.dw.artgallery.model;

import com.dw.artgallery.exception.InvalidRequestException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reserve_date", uniqueConstraints = {@UniqueConstraint(columnNames = {"artist_gallery_id","date"})})
//@UniqueConstraint: 하나의 전시에 오로지 하나의 날짜만 들어가도록 막아주는 제약조건
@Entity
public class ReserveDate {
    // 전시 일정
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "artist_gallery_id", nullable = false)
    private ArtistGallery artistGallery;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private int reservedCount = 0;

    @Version
    private Integer version;

    @OneToMany(mappedBy = "reserveDate", cascade = CascadeType.ALL)
    private List<ReserveTime> timeSlots;

    @Column(name = "remaining", nullable = false)
    private int remaining;

    public void reserve(int headcount) {
        if (this.reservedCount + headcount > this.capacity) {
            throw new InvalidRequestException("정원이 초과되었습니다.");
        }
        this.reservedCount += headcount;
        this.remaining -=headcount;
    }

    public void cancel(int headcount) {
        this.reservedCount -= headcount;
        this.remaining += headcount;
    }
    // 동시성 : 여러개의 작업이 동시에 일어나는 것처럼 보이는 상태
    // 여러 스레드가 동시에 코드를 실행하면 공유된 메모리 상에서 충돌함
    // 예약시스템에서 "남은 자리 1개" 일때
    // A와 B가 동시에 예약
    // A도 성공 , B도 성공 -> 자리가 한개인데 2명 모두 예약됨
    // -> 레포지토리 측에 락 필요: 한명만 예약처리, 다른사람은 그 작업이 끝날때까지 기다려야함
    // 비관적 락 (@Lock) 필요
}

package com.dw.artgallery.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReserveDateUpdateDTO {
    // 관리자용 날짜별 정원 및 마감일 수정 DTO
    private int newCapacity;
    private LocalDate newDeadline;
}

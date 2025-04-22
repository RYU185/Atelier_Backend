package com.dw.artgallery.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationStatDTO {
    // 막대그래프

    private String label; // 날짜별 , 요일별 , 월별 설정
    private long totalHeadcount;

}
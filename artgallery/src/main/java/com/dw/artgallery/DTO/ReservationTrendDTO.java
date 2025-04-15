package com.dw.artgallery.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ReservationTrendDTO {
    // 곡선 그래프
    private String label;
    private int cumulativeHeadcount;
    private int diff;
}

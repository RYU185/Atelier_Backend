package com.dw.artgallery.DTO;

import com.dw.artgallery.model.ReserveTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReserveTimeDTO {
    private Long id;
    private LocalTime time;
    private boolean full;

    public static ReserveTimeDTO fromEntity(ReserveTime time) {
        return new ReserveTimeDTO(
                time.getId(),
                time.getTime(),
                time.getReserveDate().isFull()
        );
    }
}
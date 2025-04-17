package com.dw.artgallery.DTO;

import com.dw.artgallery.model.ReserveDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReserveDateDTO {
    private Long id;
    private LocalDate date;
    private Integer remaining;

    public static ReserveDateDTO fromEntity(ReserveDate reserveDate){
        return new ReserveDateDTO(
                reserveDate.getId(),
                reserveDate.getDate(),
                reserveDate.getRemaining()
        );
    }
}

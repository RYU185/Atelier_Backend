package com.dw.artgallery.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// polling 용도의 DTO
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReserveAvailabilityDTO {
    private int capacity;
    private int remaining;
    private boolean isFull;
}
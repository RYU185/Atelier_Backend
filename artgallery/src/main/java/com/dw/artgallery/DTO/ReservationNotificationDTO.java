package com.dw.artgallery.DTO;

import lombok.*;

@Data
@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationNotificationDTO {
    private String title;
    private String message;
}
package com.dw.artgallery.DTO;

import lombok.*;
import org.springframework.messaging.MessageHeaders;

import java.util.Map;

@Data
@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationNotificationDTO {
    private String title;
    private String message;
}
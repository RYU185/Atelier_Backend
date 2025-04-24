package com.dw.artgallery.DTO;


import com.dw.artgallery.model.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReviewDTO {
    private String text;
    private String nickname;
    private LocalDate createdAt;

    public ReviewDTO convertToDTO(Review review) {
        return new ReviewDTO(review.getText(), review.getUser().getNickName(), review.getCreatedAt());
    }

}

package com.dw.artgallery.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class FindPwDTO {

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class SendCodeRequest {
        private String userId;
        private String email;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class VerifyCodeRequest {
        private String userId;
        private String code;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class ResetPwRequest {
        private String userId;
        private String newPassword;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class ResponseDTO {
        private String status;
        private String message;
    }
}

package com.dw.artgallery.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class FindIdDTO {

    // 요청 DTO: 이메일을 받는 DTO
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class RequestDTO {
        private String email;  // 이메일을 받을 필드
    }

    // 응답 DTO: 이메일에 해당하는 userId를 반환하는 DTO
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class ResponseDTO {
        private String status;   // 성공 또는 실패 상태
        private String message;  // 메시지 (아이디 또는 오류 메시지)
        private FindIdDataDTO data;

        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        @Setter
        public static class FindIdDataDTO {
            private String userId;  // 이메일로 찾은 아이디
        }
    }
}

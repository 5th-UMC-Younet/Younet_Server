package com.example.younet.login.dto;

import lombok.Data;

@Data
public class KakaoProfileDto {

    public Long id;
    public String connected_at;
    public Properties properties; // 사용자 property 담는 객체
    public KakaoAccount kakao_account;

    @Data
    public class Properties {
        public String name;
        public String nickname;
        public String email;
    }

    @Data
    public class KakaoAccount {
        public Boolean profile_name_needs_agreement;
        public Boolean profile_nickname_needs_agreement;
        public Profile profile;
        public Boolean profile_email_needs_agreement;
        public Boolean email_needs_agreement;
        public Boolean is_email_valid;
        public Boolean is_email_verified;
        public String email;

        @Data
        public class Profile {
            public String name;
            public String nickname;
        }
    }
}

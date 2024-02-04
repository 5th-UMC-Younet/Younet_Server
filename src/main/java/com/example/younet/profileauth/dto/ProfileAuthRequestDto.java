package com.example.younet.profileauth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileAuthRequestDto {

    private String imgUrl;
    private String mainSchool;
    private String hostCountry;
    private String hostSchool;
}

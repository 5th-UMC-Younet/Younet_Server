package com.example.younet.profileauth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileAuthRequestDto {
    @NotNull
    private String mainSchool;
    private String hostCountry;
    private String hostSchool;
}
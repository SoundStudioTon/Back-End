package org.example.soundstudiodemo.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponseDto {

    private String accessToken;
    private String refreshToken;
    private String email;
    private String name;

}

package org.example.soundstudiodemo.controller;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.soundstudiodemo.dto.RefreshTokenDto;
import org.example.soundstudiodemo.dto.UserLoginResponseDto;
import org.example.soundstudiodemo.model.RefreshToken;
import org.example.soundstudiodemo.model.Role;
import org.example.soundstudiodemo.model.User;
import org.example.soundstudiodemo.security.util.JwtTokenizer;
import org.example.soundstudiodemo.service.RefreshTokenService;
import org.example.soundstudiodemo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/userreg")
    public String registerUser(@RequestBody User user, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return bindingResult.getFieldError().getDefaultMessage();
        }

        userService.registerUser(user);
        return "redirect:/";
    }

    @PostMapping("/api/login")
    public ResponseEntity<UserLoginResponseDto> loginUser(@RequestParam String email, @RequestParam String password, HttpServletResponse response) throws IOException {
        log.info("login시작");

        User user=userService.findByEmail(email);

        List<String> roles= user.getRoles().stream()
                .map(Role::getName).collect(Collectors.toList());


        String accessToken = jwtTokenizer.createAccessToken(user.getId(),user.getEmail(),user.getName(),roles);
        String refreshToken = jwtTokenizer.createRefreshToken(user.getId(),user.getEmail(),user.getName(),roles);

        log.info("access token 뭐냐고? {}", accessToken);

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setValue(refreshToken);
        refreshTokenEntity.setUserId(user.getId());
        refreshTokenService.addRefreshToken(refreshTokenEntity);

        log.info("refresh token 뭐냐고? {}", refreshToken);

        UserLoginResponseDto loginResponse = UserLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .name(user.getName())
                .build();


        Cookie accessTokenCookie = new Cookie("accessToken",accessToken);
        accessTokenCookie.setHttpOnly(true);  //보안 (쿠키값을 자바스크립트같은곳에서는 접근할수 없어요.)
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT/1000)); //30분 쿠키의 유지시간 단위는 초 ,  JWT의 시간단위는 밀리세컨드

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT/1000)); //7일

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        log.info("Cokkie에까지 저장 완료 : {} ", loginResponse);


        return new ResponseEntity<>(loginResponse, HttpStatus.OK);

//        return "redirect:/";
    }

    @DeleteMapping("/logout")
    public ResponseEntity logout(@RequestBody RefreshTokenDto refreshTokenDto) {
        refreshTokenService.deleteRefreshToken(refreshTokenDto.getRefreshToken());
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity requestRefresh(@RequestBody RefreshTokenDto refreshTokenDto) {
        RefreshToken refreshToken = refreshTokenService.findRefreshToken(refreshTokenDto.getRefreshToken()).orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));
        Claims claims = jwtTokenizer.parseRefreshToken(refreshToken.getValue());

        Long userId = Long.valueOf((Integer)claims.get("userId"));
        String username = claims.get("username").toString();
        User user = userService.getUser(userId).orElseThrow(() -> new IllegalArgumentException("Member not found"));



        List roles = (List) claims.get("roles");
        String email = claims.getSubject();

        String accessToken = jwtTokenizer.createAccessToken(userId, username, email, roles);

        UserLoginResponseDto loginResponse = UserLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenDto.getRefreshToken())
                .userId(user.getId())
                .name(user.getName())
                .build();
        return new ResponseEntity(loginResponse, HttpStatus.OK);
    }





}

package org.example.soundstudiodemo.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.soundstudiodemo.dto.RefreshTokenDto;
import org.example.soundstudiodemo.dto.UserLoginResponseDto;
import org.example.soundstudiodemo.model.RefreshToken;
import org.example.soundstudiodemo.model.User;
import org.example.soundstudiodemo.security.util.JwtTokenizer;
import org.example.soundstudiodemo.service.RefreshTokenService;
import org.example.soundstudiodemo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;



    @PostMapping("/userreg")
    public String registerUser(@ModelAttribute("user") User user, BindingResult bindingResult) {
        User byEmail = userService.findByEmail(user.getEmail());
        if (byEmail != null) {
            bindingResult.rejectValue("email", "이미 존재하는 이메일입니다.");
        }

        userService.registerUser(user);
        return "redirect:/";
    }

    @PostMapping("/login")
    public void loginUser(@RequestParam("email") String email, @RequestParam("password") String password,
                          Model model, HttpServletResponse response) throws IOException {

        log.info("login시작");

//        if(result.hasErrors()){
//            return new ResponseEntity(HttpStatus.BAD_REQUEST);
//        }

        User user= userService.findByEmail(email);
//        if(!passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword())){
//            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
//        }

        if(user == null) {
            model.addAttribute("error","없는 아이디입니다");
        }


        String accessToken = jwtTokenizer.createAccessToken(user.getId(),user.getEmail(),user.getName());
        String refreshToken = jwtTokenizer.createRefreshToken(user.getId(),user.getEmail(),user.getName());

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


        response.sendRedirect("/");
    }

    @DeleteMapping("/logout")
    public ResponseEntity logout(@RequestBody RefreshTokenDto refreshTokenDto) {
        refreshTokenService.deleteRefreshToken(refreshTokenDto.getRefreshToken());
        return new ResponseEntity(HttpStatus.OK);
    }



}

package org.example.soundstudiodemo.controller;

import org.example.soundstudiodemo.dto.ConcentrationDto;
import org.example.soundstudiodemo.dto.UserDto;
import org.example.soundstudiodemo.model.User;
import org.example.soundstudiodemo.service.ConcentrationService;
import org.example.soundstudiodemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ConcentrationController {

    @Autowired
    private ConcentrationService concentrationService;
    @Autowired
    private UserService userService;

    @PostMapping("/sendConcentration")
    public ResponseEntity<List<ConcentrationDto>> sendConcentration(@RequestParam ("startTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")LocalDateTime startTime,
                                                                 @RequestParam("endTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")LocalDateTime endTime,
                                                                 @RequestParam("userId") Long userId){

        User user = userService.findByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }


        UserDto userDto = userService.toUserDto(user);
        List<ConcentrationDto> filteredConcentrations = userDto.getConcentrations().stream()
                .filter(concentration -> (concentration.getDate().isEqual(startTime) || concentration.getDate().isAfter(startTime)) &&
                        (concentration.getDate().isEqual(endTime) || concentration.getDate().isBefore(endTime)))
                .collect(Collectors.toList());

        if (filteredConcentrations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(filteredConcentrations);

    }

    
}

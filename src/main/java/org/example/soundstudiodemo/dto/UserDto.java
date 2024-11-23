package org.example.soundstudiodemo.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.soundstudiodemo.model.Concentration;
import org.example.soundstudiodemo.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Getter@Setter
public class UserDto {

    private Long id;
    private String email;
    private String name;
    private List<ConcentrationDto> concentrations; // DTO로 참조

}


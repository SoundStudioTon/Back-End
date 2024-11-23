package org.example.soundstudiodemo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ConcentrationDto {
    private Long id;
    private LocalDateTime date;
    private String value;
}

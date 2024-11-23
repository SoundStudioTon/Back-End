package org.example.soundstudiodemo.service;

import lombok.RequiredArgsConstructor;
import org.example.soundstudiodemo.model.Concentration;
import org.example.soundstudiodemo.model.User;
import org.example.soundstudiodemo.repository.ConcentrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcentrationService {

    private final ConcentrationRepository concentrationRepository;

    public void saveConcentration(Concentration concentration) {
        concentrationRepository.save(concentration);
    }

    public Concentration saveConcentration(String value, User user) {
        Concentration concentration = new Concentration();
        concentration.setDate(LocalDateTime.now());
        concentration.setValue(value);
        concentration.setUser(user); // 연결된 사용자 설정
        return concentrationRepository.save(concentration);
    }


}

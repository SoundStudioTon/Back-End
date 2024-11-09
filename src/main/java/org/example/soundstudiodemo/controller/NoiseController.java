package org.example.soundstudiodemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.soundstudiodemo.model.Noise;
import org.example.soundstudiodemo.model.User;
import org.example.soundstudiodemo.repository.NoiseRepository;
import org.example.soundstudiodemo.repository.UserRepository;
import org.example.soundstudiodemo.service.AIService;
import org.example.soundstudiodemo.service.NoiseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api/noise")
public class NoiseController {

    @Autowired
    private NoiseRepository noiseRepository;
    @Autowired
    private NoiseService noiseService;

    @Autowired
    private AIService aiService;  // AI 서버와 통신하는 서비스
    @Autowired
    private UserRepository userRepository;

    // 프론트엔드에서 사용자 ID와 noiseNumber를 받아와 Noise 테이블에 저장하고 AI 서버로 데이터 전송
    @PostMapping("/send")
    public ResponseEntity<String> sendNoiseToAI(@RequestParam Long userId, @RequestParam int noiseNumber) {

        noiseService.save(userId, noiseNumber);
        Noise noise= noiseService.findByUserId(userId).get();

        // AI 서버에 전송할 Noise 데이터에서 transformedNoise를 제외한 JSON 생성
        ResponseEntity<Noise> aiResponse = aiService.sendNoiseToAI(noise);

        // AI 서버에서 응답으로 변형된 소리 데이터를 받음
        if (aiResponse.getStatusCode().is2xxSuccessful() && aiResponse.getBody() != null) {
            Noise updatedNoise = aiResponse.getBody();

            // 변형된 소리 정보로 Noise 엔티티 업데이트
            noise.setFrequency(updatedNoise.getFrequency());
            noise.setVolume(updatedNoise.getVolume());
            noise.setPitch(updatedNoise.getPitch());
            noise.setReward(updatedNoise.getReward());
            noise.setPrevMethodIdx(updatedNoise.getPrevMethodIdx());
            noise.setMethodsValue(updatedNoise.getMethodsValue());
            noise.setTransformedNoise(updatedNoise.getTransformedNoise());

            // 업데이트된 정보 저장
            noiseRepository.save(noise);
            return ResponseEntity.ok("Noise updated successfully.");
        } else {
            return ResponseEntity.status(500).body("Error communicating with AI server.");
        }
    }
}

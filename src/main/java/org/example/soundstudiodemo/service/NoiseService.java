package org.example.soundstudiodemo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.soundstudiodemo.model.Noise;
import org.example.soundstudiodemo.model.User;
import org.example.soundstudiodemo.repository.NoiseRepository;
import org.example.soundstudiodemo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoiseService {

    private final NoiseRepository noiseRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public void save(Long userId, int noiseNumber){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Noise noise = new Noise();
        noise.setNoiseNumber(noiseNumber);

        noise.setUser(user);
        user.setNoise(noise);

        noiseRepository.save(noise);
        userRepository.save(user);
        log.error("완료!!");
    }

    public Optional<Noise> findByUserId(Long userId){
        Noise byUserNoiseId = noiseRepository.findByUserNoise_Id(userId);
        return Optional.ofNullable(byUserNoiseId);
    }

    public void updateNoiseReward(Long userId,int reward){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Noise noise=noiseRepository.findByUserNoise_Id(userId);
        noise.setReward(reward);
        noiseRepository.save(noise);

    }

}

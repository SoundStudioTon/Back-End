package org.example.soundstudiodemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.soundstudiodemo.model.Noise;
import org.example.soundstudiodemo.model.User;
import org.example.soundstudiodemo.repository.NoiseRepository;
import org.example.soundstudiodemo.repository.UserRepository;
import org.example.soundstudiodemo.security.util.JwtTokenizer;
import org.example.soundstudiodemo.service.AIService;
import org.example.soundstudiodemo.service.NoiseService;
import org.example.soundstudiodemo.service.UserService;
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
    private UserService userService;

    @Autowired
    private AIService aiService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenizer jwtTokenizer;

    @PostMapping("/isnoisethere")
    public ResponseEntity<Boolean> isNoiseThere(@RequestParam String AccessToken) {
        log.error("작동시작");
        Long userIdFromToken=jwtTokenizer.getUserIdFromToken(AccessToken);
        log.error(userIdFromToken.toString());
        Optional<User> user=userRepository.findById(userIdFromToken);
        if(user.get().getNoise()!=null){
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }

    @PostMapping("/savefirstnoise")
    public ResponseEntity<String> saveFirstNoise(@RequestParam String AccessToken, @RequestParam int noiseNumber) {
        Long userIdFromToken=jwtTokenizer.getUserIdFromToken(AccessToken);
        noiseService.save(userIdFromToken, noiseNumber);
        return ResponseEntity.ok("successfully saved first noise");
    }

    @PostMapping("/getnoisenumber")
    public ResponseEntity<Integer> getNoiseNumber(@RequestParam String AccessToken) {
        Long userIdFromToken=jwtTokenizer.getUserIdFromToken(AccessToken);
        int noiseNumber=userService.getUserNoiseNumber(userIdFromToken);
        return ResponseEntity.ok(noiseNumber);

    }

    @PostMapping("/send")
    public ResponseEntity<String> sendNoiseToAI(@RequestParam String AccessToken) {

        Long userId=jwtTokenizer.getUserIdFromToken(AccessToken);


        Noise noise= noiseService.findByUserId(userId).get();

        ResponseEntity<Noise> aiResponse = aiService.sendNoiseToAI(noise);

        if (aiResponse.getStatusCode().is2xxSuccessful() && aiResponse.getBody() != null) {
            Noise updatedNoise = aiResponse.getBody();

            noise.setFrequency_1(updatedNoise.getFrequency_1());
            noise.setFrequency_2(updatedNoise.getFrequency_2());
            noise.setFrequency_3(updatedNoise.getFrequency_3());
            noise.setFrequency_4(updatedNoise.getFrequency_4());
            noise.setVolume(updatedNoise.getVolume());
            noise.setReward(updatedNoise.getReward());
            noise.setPrevMethodIdx(updatedNoise.getPrevMethodIdx());
            noise.setMethodsValue(updatedNoise.getMethodsValue());
            noise.setTransformedNoise(updatedNoise.getTransformedNoise());

            noiseRepository.save(noise);
            return ResponseEntity.ok("Noise updated successfully.");
        } else {
            return ResponseEntity.status(500).body("Error communicating with AI server.");
        }
    }

    @PostMapping("/noiseRewardUpdate")
    public ResponseEntity<String> noiseRewardUpdate(@RequestParam String AccessToken, @RequestParam int reward) {
        Long userIdFromToken=jwtTokenizer.getUserIdFromToken(AccessToken);

        noiseService.updateNoiseReward(userIdFromToken, reward);
        return ResponseEntity.ok("successfully noise reward");
    }

}

package org.example.soundstudiodemo.service;

import lombok.extern.slf4j.Slf4j;
import org.example.soundstudiodemo.model.Noise;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AIService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String aiServerUrl = "http://soundstudio-ai.kro.kr:5000/transform";

    public ResponseEntity<Noise> sendNoiseToAI(Noise noise) {
        Map<String, Object> request = new HashMap<>();
        request.put("userId", noise.getId());
        request.put("noise_number", noise.getNoiseNumber());
        request.put("freq_1", noise.getFrequency_1());
        request.put("freq_2", noise.getFrequency_2());
        request.put("freq_3", noise.getFrequency_3());
        request.put("freq_4", noise.getFrequency_4());
        request.put("volume", noise.getVolume());
        request.put("reward", noise.getReward());
        request.put("prev_method_idx", noise.getPrevMethodIdx());
        request.put("methods_value_list", noise.getMethodsValue());
        log.error(request.toString());

        HttpHeaders headers = new HttpHeaders();


        headers.setContentType(MediaType.APPLICATION_JSON);
        log.error(headers.toString());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        log.error(entity.toString());



        return restTemplate.postForEntity(aiServerUrl, entity, Noise.class);
    }

}

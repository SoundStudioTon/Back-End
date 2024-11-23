package org.example.soundstudiodemo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.soundstudiodemo.model.User;
import org.example.soundstudiodemo.service.ConcentrationService;
import org.example.soundstudiodemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    @Autowired
    private ConcentrationService concentrationService;
    @Autowired
    private UserService userService;

    @GetMapping("/previewImage")
    public ResponseEntity<byte[]> previewImage(@RequestParam("file") MultipartFile file) throws IOException {
        byte[] imageBytes = file.getBytes();
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // 이미지 형식에 맞게 설정
                .body(imageBytes);
    }

    @PostMapping("/uploadImage")
    public ResponseEntity<String> handleImageUpload(@RequestParam("file") MultipartFile file, @RequestParam("userId") Long userId) {
        try{
            String aiResponse = sendImageToAiModel(file);

            // AI 모델의 예측 값 추출
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJson = objectMapper.readTree(aiResponse);
            String prediction = responseJson.get("transformedPrediction").asText();

            // 사용자 엔티티 로드 (UserService를 통해 로드한다고 가정)
            User user = userService.findByUserId(userId); // UserService에서 구현 필요

            // Concentration 저장
            concentrationService.saveConcentration(prediction, user);

            return ResponseEntity.ok(aiResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 업로드에 실패하였습니다.");
        }
    }

    private String sendImageToAiModel(MultipartFile file) throws IOException {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("file",new ByteArrayResource(file.getBytes()){
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(body, headers);


        log.error("파일명: " + file.getOriginalFilename() + ", 크기: " + file.getSize());

        String aiServerUrl = "http://soundstudio-ai.kro.kr:5000/predict";
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(aiServerUrl, requestEntity, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String prediction = jsonNode.get("prediction").asText();
            String transformedResponse = objectMapper.createObjectNode()
                    .put("transformedPrediction", prediction)
                    .toString();

            return transformedResponse;
        } catch (RestClientException e) {
            e.printStackTrace();
            throw new RuntimeException("AI 서버 호출에 실패했습니다.", e);
        }
    }

    private static byte[] convertHexToBytes(String hex) {
        int length = hex.length();
        byte[] data = new byte[length / 4];
        for (int i = 0; i < length; i += 4) {
            data[i / 4] = (byte) Integer.parseInt(hex.substring(i, i + 4), 16);
        }
        return data;
    }



    @PostMapping("/sendToFront")
    public ResponseEntity<String> sendToFront(@RequestBody String focusDataJson) {
        try {
            return ResponseEntity.ok(focusDataJson);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("프론트로 전송에 실패하였습니다.");
        }
    }

}

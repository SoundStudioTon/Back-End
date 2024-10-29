package org.example.soundstudiodemo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping("/previewImage")
    public ResponseEntity<byte[]> previewImage(@RequestParam("file") MultipartFile file) throws IOException {
        byte[] imageBytes = file.getBytes();
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // 이미지 형식에 맞게 설정
                .body(imageBytes);
    }

    @PostMapping("/uploadImage")
    public ResponseEntity<String> handleImageUpload(@RequestParam("file") MultipartFile file) {
        try{
            String aiResponse = sendImageToAiModel(file);

            return ResponseEntity.ok(aiResponse);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 업로드에 실패하였습니다.");
        }
    }

    private String sendImageToAiModel(MultipartFile file) throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        log.error("실행 완");

        HttpHeaders headers= new HttpHeaders();
        log.error("실행 완");
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        log.error("실행 완");

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("file",new ByteArrayResource(file.getBytes()){
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(body, headers);


        log.error("파일명: " + file.getOriginalFilename() + ", 크기: " + file.getSize());

        String aiServerUrl = "http://172.16.25.111:5000/predict";
        try {
            log.error("블라블라블라"+ restTemplate.getMessageConverters());
            ResponseEntity<String> response = restTemplate.postForEntity(aiServerUrl, requestEntity, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String prediction = jsonNode.get("prediction").asText();
            String transformedResponse = objectMapper.createObjectNode()
                    .put("transformedPrediction", prediction)
                    .toString();

            return transformedResponse;
        } catch (RestClientException e) {
            e.printStackTrace();  // 예외 로그 출력
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

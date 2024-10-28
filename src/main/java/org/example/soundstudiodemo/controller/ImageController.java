package org.example.soundstudiodemo.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class ImageController {

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

        String aiServerUrl = "";
        ResponseEntity<String> response = restTemplate.postForEntity(aiServerUrl, requestEntity, String.class);

        return response.getBody();
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

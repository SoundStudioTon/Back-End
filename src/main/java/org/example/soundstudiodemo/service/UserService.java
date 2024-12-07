package org.example.soundstudiodemo.service;

import lombok.AllArgsConstructor;
import org.example.soundstudiodemo.dto.ConcentrationDto;
import org.example.soundstudiodemo.dto.UserDto;
import org.example.soundstudiodemo.model.Concentration;
import org.example.soundstudiodemo.model.Noise;
import org.example.soundstudiodemo.model.Role;
import org.example.soundstudiodemo.model.User;
import org.example.soundstudiodemo.repository.RoleRepository;
import org.example.soundstudiodemo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Transactional
    public User registerUser(User user) {
        user.setEmail(user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setName(user.getName());
        Role userRole;
        if(user.getEmail().equals("gsbtiger0215@naver.com")){
            userRole=roleRepository.findByName("ADMIN");
        }else{
            userRole=roleRepository.findByName("USER");
        }
        user.setRoles(Collections.singleton(userRole));
        return userRepository.save(user);
    }

    @Transactional
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User findByUserId(Long id){
        return userRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUser(Long id){
        return userRepository.findById(id);
    }

    public int getUserNoiseNumber(Long userId){
        User user=userRepository.findById(userId).orElse(null);
        Noise noise= user.getNoise();
        int noiseNumber=noise.getNoiseNumber();
        return noiseNumber;
    }

    public void findConcentrationByUserId(Long userId) {
        List<Concentration> concentrations = userRepository.getConcentrationById(userId);
    }

    public UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());

        // Concentration 엔티티를 ConcentrationDto로 변환
        List<ConcentrationDto> concentrationDtos = user.getConcentrations().stream()
                .map(this::toConcentrationDto)
                .collect(Collectors.toList());
        userDto.setConcentrations(concentrationDtos);

        return userDto;
    }

    public ConcentrationDto toConcentrationDto(Concentration concentration) {
        ConcentrationDto dto = new ConcentrationDto();
        dto.setId(concentration.getId());
        dto.setDate(concentration.getDate());
        dto.setValue(concentration.getValue()); // 필요하면 추가적인 필드 매핑
        return dto;
    }


}

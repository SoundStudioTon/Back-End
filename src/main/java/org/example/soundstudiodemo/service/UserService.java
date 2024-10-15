package org.example.soundstudiodemo.service;

//import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.soundstudiodemo.model.Role;
import org.example.soundstudiodemo.model.User;
import org.example.soundstudiodemo.repository.RoleRepository;
import org.example.soundstudiodemo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collections;
import java.util.Optional;

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

    @Transactional(readOnly = true)
    public Optional<User> getUser(Long id){
        return userRepository.findById(id);
    }





}

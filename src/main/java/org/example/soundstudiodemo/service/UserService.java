package org.example.soundstudiodemo.service;

import jakarta.transaction.Transactional;
import org.example.soundstudiodemo.model.User;
import org.example.soundstudiodemo.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;

    @Transactional
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }



}

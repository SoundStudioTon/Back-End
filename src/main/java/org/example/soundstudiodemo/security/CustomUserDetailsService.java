package org.example.soundstudiodemo.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.soundstudiodemo.model.User;
import org.example.soundstudiodemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User.UserBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.info("유저네임 확인중이다알아너라: {}", email);
        User user= userRepository.findByEmail(email);

        if(user==null){
            throw new UsernameNotFoundException(email);
        }

        log.info("유저 찾았다라라라ㅏ{}",user);

        UserBuilder userBuilder = org.springframework.security.core.userdetails.User.withUsername(user.getEmail());
        userBuilder.password(user.getPassword());

        log.info("유저 딩테일스 생성 완료 {}",email);

        return userBuilder.build();
    }



}
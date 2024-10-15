package org.example.soundstudiodemo.repository;

import org.example.soundstudiodemo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);
    User findById(long id);
    User save(User user);
}

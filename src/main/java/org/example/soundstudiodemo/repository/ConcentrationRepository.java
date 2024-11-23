package org.example.soundstudiodemo.repository;

import org.example.soundstudiodemo.model.Concentration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcentrationRepository extends JpaRepository<Concentration, Long> {
    Concentration save(Concentration concentration);
    Concentration findByUserId(Long userId);
}

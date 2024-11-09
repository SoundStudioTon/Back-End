package org.example.soundstudiodemo.repository;

import org.example.soundstudiodemo.model.Noise;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NoiseRepository extends JpaRepository<Noise, Long> {
    Noise findByUserNoise_Id(Long userId);
}

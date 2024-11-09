package org.example.soundstudiodemo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="noise")
@Getter
@Setter
@NoArgsConstructor
public class Noise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "noise_number", nullable = false)
    private int noiseNumber;

    @Column(nullable = false, columnDefinition = "FLOAT DEFAULT 0")
    private float frequency = 0.0f;

    @Column(nullable = false, columnDefinition = "FLOAT DEFAULT 0")
    private float volume = 0.0f;

    @Column(nullable = false, columnDefinition = "FLOAT DEFAULT 0")
    private float pitch = 0.0f;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int reward = 0;

    @Column(name = "prev_method_idx", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int prevMethodIdx = 0;

    @Column(name = "methods_value_list", columnDefinition = "JSON")
    private String methodsValue = "[0.0, 0.0, 0.0, 0.0, 0.0, 0.0]";

    @Lob
    private byte[] transformedNoise;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")  // 외래 키 연결
    private User user;
}

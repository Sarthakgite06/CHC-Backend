package com.onkar.chc.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DOSAGE_OF_MEDICINE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DosageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Integer doseId;

    @Column(name = "MORNING_DOSE",nullable = false)
    private Boolean morning;

    @Column(name = "AFTERNOON_DOSE",nullable = false)
    private Boolean afternoon;

    @Column(name = "NIGHT_DOSE",nullable = false)
    private Boolean night;

}

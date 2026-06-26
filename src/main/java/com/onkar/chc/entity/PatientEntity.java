package com.onkar.chc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "PATIENT_LIST")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientEntity {

    @Id
    @Column(name = "PATIENT_HEALTH_CARD_ID")
    private String healthCardNo;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "DOB")
    private String dob;

    @Column(name = "AGE")
    private Integer age;

    @Column(name = "WEIGHT")
    private Integer weight;

    @Column(name = "GENDER")
    private String gender;

    @Column(name = "BLOOD_PRESSURE")
    private String bloodPressure;
}

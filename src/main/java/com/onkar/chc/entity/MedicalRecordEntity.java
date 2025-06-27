package com.onkar.chc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "MEDICAL_RECORDS_OF_PATIENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    @Column(name = "RECORD_ID")
    private String medicalRecordId;

    @Column(name = "CREATED_DATE")
    private String createdDate;

    @Column(name = "DOCTOR_REG_NO",nullable = false)
    private Long doctorRegNo;

    @OneToMany(cascade = CascadeType.ALL)
    private List<MedicineInfoEntity> medicineInfoEntities;

    @ManyToOne(cascade = CascadeType.DETACH)
    private PatientEntity patientEntity;
}

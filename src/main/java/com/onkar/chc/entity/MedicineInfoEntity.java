package com.onkar.chc.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MEDICINE_INFO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "MEDICINE_INFO_ID")
    private String medicineInfoId;

    @Column(name = "MEDICINE_NAME",nullable = false,length = 200)
    private String medicineName;
    
    @OneToOne(cascade = CascadeType.ALL)
    //@Column(name = "DOSE_ID",nullable = false)
    private DosageEntity dosageEntity;

    @Column(name = "NO_OF_DAYS",nullable = false)
    private Integer days;

    @Column(name = "REMARK")
    private String remark;
}

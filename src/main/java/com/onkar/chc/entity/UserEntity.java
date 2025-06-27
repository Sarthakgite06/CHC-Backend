package com.onkar.chc.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private Integer userId;

    //User Name
    @Column(name = "USER_ID",nullable = false,unique = true)
    private String userName;

    //First Name
    @Column(name = "FIRST_NAME",nullable = false)
    private String firstName;

    //Last Name
    @Column(name = "LAST_NAME",nullable = false)
    private String lastName;

    //Password
    @Column(name = "PASSWORD",nullable = false)
    private String password;

    //Email
    @Column(name = "EMAIL")
    private String email;

    //Contact No.
    @Column(name = "CONTACT_NO",nullable = false)
    private Long contactNo;

    //Health Card No
    @Column(name = "HEALTH_CARD_NO",nullable = false,unique = true)
    private Integer healthCardNo;

    //User role
    @Column(name = "USER_ROLE",nullable = false)
    private String role;

    //Date of Birth.
    @Column(name = "DOB")
    private String dob;

    //Doctor Id
    @Column(name = "DOCTOR_REGI_NO",unique = true)
    private Long doctorRegiNo;

    //Chemist Id
    @Column(name = "CHEMIST_REGI_NO",unique = true)
    private Long chemistRegiNo;

    //Address
    @Column(name = "ADDRESS")
    private String address;

    //Gender
    @Column(name = "GENDER",nullable = false)
    private String gender;

    @Column(name = "BLOOD_GROUP",nullable = false)
    private String bloodGroup;

}

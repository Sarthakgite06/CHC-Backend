package com.onkar.chc.responseDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserResponseDTO {

    private Integer userId;

    //User Name
    private String userName;

    //First Name
    private String firstName;

    //Last Name
    private String lastName;

    //Password
    private String password;

    //Email
    private String email;

    //Contact No.
    private Long contactNo;

    //Health Card No
    private Integer healthCardNo;

    //User role
    private String role;

    //Date of Birth.
    private String dob;

    //Doctor Id
    private Long doctorRegiNo;

    //Chemist Id
    private Long chemistRegiNo;

    //Address
    private String address;

    //Gender
    private String gender;

    //BloodGroup
    private String bloodGroup;
}

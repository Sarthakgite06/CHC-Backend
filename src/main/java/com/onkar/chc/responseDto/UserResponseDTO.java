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

    //Email
    private String email;

    //Contact No.
    private Long contactNo;

    //Health Card ID - auto-generated format: PUN00000001
    private String healthCardNo;

    //District
    private String district;

    //User role
    private String role;

    //Date of Birth.
    private String dob;

    //Doctor Id
    private Long doctorRegiNo;

    //Chemist Id
    private Long chemistRegiNo;

    //Pathologist License No
    private String pathologistLicenseNo;

    //Address
    private String address;

    //Gender
    private String gender;

    //BloodGroup
    private String bloodGroup;

    //Registration date
    private String createdAt;
}

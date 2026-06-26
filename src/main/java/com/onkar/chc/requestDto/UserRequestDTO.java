package com.onkar.chc.requestDto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO {

    //User Name
    @NotNull(message = "Enter valid userName.")
    @NotEmpty(message = "Enter valid userName.")
    @NotBlank(message = "Enter valid userName.")
    @Size(max = 50)
    private String userName;

    //First Name
    @NotBlank(message = "Enter valid First Name.")
    private String firstName;

    //Last Name
    @NotBlank(message = "Enter valid Last Name.")
    private String lastName;

    //Password
    @NotNull
    @NotBlank
    @NotEmpty
    private String password;

    //Email
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",message = "Enter valid email address.")
    private String email;

    //Contact No.
    @Digits(integer = 12,fraction = 0)
    private Long contactNo;

    // Health Card ID is auto-generated — NOT user-supplied
    // District is used instead for generation
    @NotBlank(message = "Select a valid district.")
    private String district;

    //User role
    @NotBlank(message = "Enter valid role.")
    private String role;

    //Date of Birth.
    @NotNull
    @NotBlank
    @NotEmpty
    private String dob;

    //Doctor Id - credential for Doctor role
    private Long doctorRegiNo;

    //Chemist Id - credential for Chemist role
    private Long chemistRegiNo;

    //Pathologist License No - credential for Pathologist role
    private String pathologistLicenseNo;

    //Address
    @NotNull
    @NotEmpty
    private String address;

    //Gender
    @NotNull
    @NotBlank
    @NotEmpty
    private String gender;

    //BloodGroup
    @NotNull
    private String bloodGroup;
}

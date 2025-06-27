package com.onkar.chc.requestDto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

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
    @Pattern(regexp = "^(?=.[a-z])(?=.[A-Z])(?=.\\\\d)(?=.[@$!%?&])[A-Za-z\\\\d@$!%?&]{8,20}$",message = "Enter valid password.")
    private String password;

    //Email
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",message = "Enter valid email address.")
    private String email;

    //Contact No.
    @Digits(integer = 12,fraction = 0)
    private Long contactNo;

    //Health Card No
    private Integer healthCardNo;

    //User role
    @NotBlank(message = "Enter valid role.")
    private String role;

    //Date of Birth.
    @NotNull
    @NotBlank
    @NotEmpty
    private String dob;

    //Doctor Id
    private Long doctorRegiNo;

    //Chemist Id
    private Long chemistRegiNo;

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

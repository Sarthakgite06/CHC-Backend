package com.onkar.chc.responseDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponseDTO {

    private Integer healthCardNo;

    private String userName;

    private String dob;

    private Integer age;

    private Integer weight;

    private String gender;

    private String bloodPressure;
}

package com.onkar.chc.security.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponseDTO {

    private String token;
    private String userName;
    private String role;
    private String healthCardNo;
    private String district;
}

package com.onkar.chc.security.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtRequestDTO {
    private String userName;
    private String password;
}

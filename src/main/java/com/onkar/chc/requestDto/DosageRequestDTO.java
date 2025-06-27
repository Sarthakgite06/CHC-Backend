package com.onkar.chc.requestDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DosageRequestDTO {

    private Boolean morning;

    private Boolean afternoon;

    private Boolean night;
}

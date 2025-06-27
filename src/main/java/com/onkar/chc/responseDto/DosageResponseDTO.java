package com.onkar.chc.responseDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DosageResponseDTO {

    private Integer doseId;

    private Boolean morning;

    private Boolean afternoon;

    private Boolean night;
}

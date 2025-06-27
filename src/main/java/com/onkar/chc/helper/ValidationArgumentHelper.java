package com.onkar.chc.helper;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationArgumentHelper {

    private String field;
    private String defaultMessage;
}

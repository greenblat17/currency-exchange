package com.greenblat.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Currency {
    private Integer id;
    private String code;
    private String fullName;
    private String sign;
}

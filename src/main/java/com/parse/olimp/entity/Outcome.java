package com.parse.olimp.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter @Setter @ToString
@EqualsAndHashCode
public class Outcome {
    private String id;
    private String name;
    private double statKef;
}

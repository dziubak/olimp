package com.parse.olimp.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter @ToString
@EqualsAndHashCode
public class Market {
    private String name;
    private List<Outcome> outcomes;
}

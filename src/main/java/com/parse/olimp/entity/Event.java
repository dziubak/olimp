package com.parse.olimp.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter @Setter @ToString
@EqualsAndHashCode
public class Event {
    private int id;
    private String link;

    private String name;
    private LocalDate date;
    private LocalTime time;

    private List<Market> markets;
}

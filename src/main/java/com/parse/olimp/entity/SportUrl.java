package com.parse.olimp.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@EqualsAndHashCode
public class SportUrl {
    private String soccer;
    private String tennis;
    private String hockey;
    private String basketball;
    private String baseball;
    private String rugbyLeague;
    private String rugbyUnion;
}

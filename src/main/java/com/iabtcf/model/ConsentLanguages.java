package com.iabtcf.model;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
public class ConsentLanguages {

    private  Set<String> languages = new HashSet<String>() {{
        add("EN");
        add("BG");
        add("CS");
        add("DA");
        add("DE");
        add("EL");
        add("ES");
        add("ET");
        add("FI");
        add("FR");
        add("GA");
        add("HR");
        add("HU");
        add("IT");
        add("LT");
        add("LV");
        add("MT");
        add("NL");
        add("PL");
        add("PT");
        add("RO");
        add("SK");
        add("SL");
        add("SV");
    }};

    private ConsentLanguages() {
    }
    private static final ConsentLanguages instance = new ConsentLanguages();
    public static ConsentLanguages getInstance() {
        return instance;
    }
}

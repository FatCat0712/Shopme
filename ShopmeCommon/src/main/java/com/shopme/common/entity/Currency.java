package com.shopme.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "currencies")
@Getter
@Setter
public class Currency extends IdBasedEntity{

    @Column(length = 64, nullable = false)
    private String name;

    @Column(length = 3, nullable = false)
    private String symbol;

    @Column(length = 4, nullable = false)
    private String code;

    public Currency() {
    }

    public Currency(String name, String symbol, String code) {
        this.name = name;
        this.symbol = symbol;
        this.code = code;
    }

    @Override
    public String toString() {
        return String.format("%s - %s - %s", name, code, symbol);
    }
}

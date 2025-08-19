package com.shopme.common.entity.state;

import com.shopme.common.entity.country.Country;
import com.shopme.common.entity.IdBasedEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "states")
@Getter
@Setter
public class State extends IdBasedEntity {
    @Column(length = 46, unique = true ,nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    public State() {
    }


    public State(String name) {
        this.name = name;
    }

    public State(String name, Country country) {
        this.name = name;
        this.country = country;
    }

    public State(Integer id, String name, Country country) {
        this.id = id;
        this.name = name;
        this.country = country;
    }



    @Override
    public String toString() {
        return "State{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country=" + country.getName() +
                '}';
    }
}

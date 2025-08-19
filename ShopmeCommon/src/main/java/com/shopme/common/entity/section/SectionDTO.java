package com.shopme.common.entity.section;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SectionDTO {
    private Integer id;
    private String heading;
    private String description;
    private SectionType sectionType;
    private int position;
    private boolean enabled;
    private List<Integer> sectionArticles;
    private List<Integer> sectionBrands;
    private List<Integer> sectionCategories;

    @Override
    public String toString() {
        return "SectionDTO{" +
                "id=" + id +
                ", heading='" + heading + '\'' +
                ", description='" + description + '\'' +
                ", sectionType=" + sectionType +
                ", position=" + position +
                ", enabled=" + enabled +
                ", sectionArticles=" + sectionArticles +
                ", sectionBrands=" + sectionBrands +
                ", sectionCategory=" + sectionCategories +
                '}';
    }
}

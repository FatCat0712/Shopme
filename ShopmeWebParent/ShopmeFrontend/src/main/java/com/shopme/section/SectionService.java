package com.shopme.section;

import com.shopme.common.entity.section.Section;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectionService {
    private final SectionRepository repo;

    @Autowired
    public SectionService(SectionRepository repo) {
        this.repo = repo;
    }

    public List<Section> listAll() {
        return repo.findEnabledSections();
    }
}

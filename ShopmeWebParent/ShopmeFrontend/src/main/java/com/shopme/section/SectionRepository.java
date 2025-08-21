package com.shopme.section;

import com.shopme.common.entity.section.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Integer> {
    @Query("SELECT s FROM Section s WHERE s.enabled = true ORDER BY s.position")
    List<Section> findEnabledSections();
}

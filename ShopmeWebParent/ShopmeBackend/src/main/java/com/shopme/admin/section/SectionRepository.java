package com.shopme.admin.section;

import com.shopme.common.entity.section.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionRepository extends JpaRepository<Section, Integer> {
    @Query("UPDATE Section s SET s.enabled = ?2 WHERE s.id = ?1")
    @Modifying
    void enableSection(Integer sectionId, Boolean status);

    Section findByPosition(Integer position);

    @Query("SELECT MAX(s.position) FROM Section s")
    Integer findLastPosition();

    @Query("UPDATE Section s SET s.position = s.position - 1 WHERE s.position > ?1 ")
    @Modifying
    void updatePositionOfAllSection(Integer position);


}

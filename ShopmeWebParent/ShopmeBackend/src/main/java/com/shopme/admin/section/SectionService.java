package com.shopme.admin.section;

import com.shopme.common.entity.section.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SectionService {
    private final SectionRepository repo;
    public static final int SECTION_BY_PAGE = 10;

    @Autowired
    public SectionService(SectionRepository repo) {
        this.repo = repo;
    }

    public Page<Section> listByPage(int pageNum) {
       Sort sort =  Sort.by("position").ascending();
        Pageable pageable = PageRequest.of(pageNum - 1, SECTION_BY_PAGE, sort);
        return repo.findAll(pageable);
    }

    public Section get(Integer sectionId) throws SectionNotFoundException {
        Optional<Section> section = repo.findById(sectionId);
        if(section.isEmpty()) {
            throw new SectionNotFoundException("Could not find any sections with ID " + sectionId);
        }
        return section.get();
    }

    public void enableSection(Integer sectionId, Boolean status) throws SectionNotFoundException {
        try {
            Section section = get(sectionId);
            repo.enableSection(section.getId(), status);
        } catch (SectionNotFoundException e) {
            throw new SectionNotFoundException(e.getMessage());
        }
    }

    public void updatePosition(Integer sectionId, String action) throws SectionNotFoundException {
        try {
            Section section = get(sectionId);
            int currentPosition = section.getPosition();
            int currentNumOfEnabledSections = repo.findAll().size();
            int newPosition = 0;
            Section currentSectionByNewPosition;
            if("up".equals(action)) {
                newPosition =  currentPosition - 1;
                if(newPosition < 1) {
                    throw new InvalidSectionPosition(String.format("The section with ID %d is already at the first position", sectionId));
                }
            }
            else if("down".equals(action)) {
                newPosition = currentPosition + 1;
                if(newPosition > currentNumOfEnabledSections) {
                    throw new InvalidSectionPosition(String.format("The section with ID %d is already at the last position", sectionId));
                }
            }

            currentSectionByNewPosition = repo.findByPosition(newPosition);
            currentSectionByNewPosition.setPosition(currentPosition);
            section.setPosition(newPosition);

            repo.save(currentSectionByNewPosition);
            repo.save(section);
        } catch (SectionNotFoundException e) {
            throw new SectionNotFoundException(e.getMessage());
        }
    }



    public void save(Section sectionInForm) throws SectionNotFoundException {
        Integer sectionId = sectionInForm.getId();
        Section sectionAtLastPosition = repo.findByLastPosition();
        SectionType sectionTypeInForm = sectionInForm.getSectionType();
        Section section;
        if(sectionId != null) {
            section = get(sectionId);
            if(section.getSectionType().equals(SectionType.SELECTED_CATEGORIES) && sectionTypeInForm.equals(SectionType.SELECTED_CATEGORIES)) {
                section.setSectionCategories(sectionInForm.getSectionCategories());
            }
            else if(section.getSectionType().equals(SectionType.SELECTED_ARTICLES) && sectionTypeInForm.equals(SectionType.SELECTED_ARTICLES)) {
                section.setSectionArticles(sectionInForm.getSectionArticles());
            }
            else if(section.getSectionType().equals(SectionType.SELECTED_BRANDS) && sectionTypeInForm.equals(SectionType.SELECTED_BRANDS)) {
                section.setSectionBrands(sectionInForm.getSectionBrands());
            }
        }
        else {
            section = new Section();
        }

        section.setHeading(sectionInForm.getHeading());
        section.setDescription(sectionInForm.getDescription());
        section.setSectionType(sectionTypeInForm);
        section.setEnabled(sectionInForm.isEnabled());
        section.setPosition(sectionAtLastPosition.getPosition() + 1);

       if(sectionTypeInForm.equals(SectionType.SELECTED_CATEGORIES)) {
           List<SectionCategory> categories = sectionInForm.getSectionCategories();
           for(SectionCategory c : categories) {
               c.setSection(section);
           }
          section.setSectionCategories(categories);
       }
       else if(sectionTypeInForm.equals(SectionType.SELECTED_ARTICLES)) {
           List<SectionArticle> articles = sectionInForm.getSectionArticles();
           for(SectionArticle a: articles) {
               a.setSection(section);
           }
           section.setSectionArticles(articles);
       }
       else if(sectionTypeInForm.equals(SectionType.SELECTED_BRANDS)) {
           List<SectionBrand> brands = sectionInForm.getSectionBrands();
           for(SectionBrand b : brands) {
               b.setSection(section);
           }
           section.setSectionBrands(brands);
       }

        repo.save(section);

    }



}

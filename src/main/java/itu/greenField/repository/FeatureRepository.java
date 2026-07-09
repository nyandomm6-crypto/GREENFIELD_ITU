package itu.greenField.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.Feature;

public interface FeatureRepository extends JpaRepository<Feature, Long> {
    List<Feature> findBySectionOrderByIdAsc(String section);

    boolean existsBySection(String section);
}

package itu.greenField.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.Temoignage;

public interface TemoignageRepository extends JpaRepository<Temoignage, Integer> {
    List<Temoignage> findByIsActifTrueOrderByIdDesc();
}

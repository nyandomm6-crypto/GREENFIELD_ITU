package itu.greenField.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.Faq;

public interface FaqRepository extends JpaRepository<Faq, Integer> {
    List<Faq> findByActiveTrueOrderByOrdreAsc();
}

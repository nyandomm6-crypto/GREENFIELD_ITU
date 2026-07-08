package itu.greenField.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.Photo;

public interface PhotoRepository extends JpaRepository<Photo, Integer> {
    List<Photo> findByProduit_Id(Integer idProduit);

    Photo findFirstByProduit_IdOrderByIdAsc(Integer idProduit);
}

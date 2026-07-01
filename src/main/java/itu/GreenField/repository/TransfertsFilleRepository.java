package itu.GreenField.repository;

import itu.GreenField.model.TransfertsFille;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransfertsFilleRepository extends JpaRepository<TransfertsFille, Long> {
    List<TransfertsFille> findByTransfertId(Long idTransfert);
}

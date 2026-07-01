package itu.greenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.Client;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    Client findByMail(String email);
}

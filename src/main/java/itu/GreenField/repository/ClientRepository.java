package itu.greenfield.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenfield.model.Client;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    Client findByMail(String email);
}

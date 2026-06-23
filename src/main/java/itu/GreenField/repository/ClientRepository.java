package itu.GreenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.GreenField.model.Client;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    public Client findByMail(String email);
    
}

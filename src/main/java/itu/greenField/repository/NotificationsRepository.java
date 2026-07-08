package itu.greenfield.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenfield.model.Notifications;

public interface NotificationsRepository extends JpaRepository<Notifications, Integer> {
}

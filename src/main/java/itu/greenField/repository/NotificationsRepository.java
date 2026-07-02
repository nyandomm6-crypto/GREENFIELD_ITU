package itu.greenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.greenField.model.Notifications;

public interface NotificationsRepository extends JpaRepository<Notifications, Integer> {
}

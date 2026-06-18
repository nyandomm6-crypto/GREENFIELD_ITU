package itu.GreenField.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itu.GreenField.model.Notifications;

public interface NotificationsRepository extends JpaRepository<Notifications, Integer> {
}

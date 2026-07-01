package itu.GreenField.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "validation_mail")
public class ValidationMail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @Column(nullable = false, length = 5)
    private String token;

    @Column(name = "date_expiration", nullable = false)
    private LocalDateTime dateExpiration;

    @Column(name = "est_verifie", nullable = false)
    private Boolean estVerifie = false;

    // ======================
    // GETTERS & SETTERS
    // ======================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDateTime dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public Boolean getEstVerifie() {
        return estVerifie;
    }

    public void setEstVerifie(Boolean estVerifie) {
        this.estVerifie = estVerifie;
    }
}
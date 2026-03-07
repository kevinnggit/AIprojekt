package com.nspace.model;

import jakarta.persistence.*;

/**
 * JPA-Entität, die einen Systembenutzer repräsentiert.
 *
 * <p>Benutzer werden in der Tabelle {@code app_users} gespeichert. Der Name der Tabelle
 * weicht bewusst vom Klassennamen ab, da {@code user} ein reserviertes Schlüsselwort
 * in PostgreSQL ist. Jeder Benutzer besitzt genau eine Rolle, die von Spring Security
 * zur Autorisierung ausgewertet wird.</p>
 */
@Entity
@Table(name = "app_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // Zulässige Werte: "ROLE_USER" und "ROLE_ADMIN" – das Präfix "ROLE_" ist
    // von Spring Security vorgeschrieben und wird zur Auswertung von hasRole() benötigt
    @Column(nullable = false)
    private String role; // "ROLE_USER", "ROLE_ADMIN"

    public User() {
    }

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

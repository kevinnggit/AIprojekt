package com.nspace.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * JPA-Entität für einen Portfolio-Eintrag.
 *
 * <p>Repräsentiert ein einzelnes Projekt oder Werk im öffentlich sichtbaren Portfolio.
 * Lombok-Annotationen übernehmen die Generierung von Getter, Setter, Konstruktoren
 * und {@code toString()}, um Boilerplate-Code zu vermeiden. Tags werden als
 * kommagetrennte Zeichenkette gespeichert, um die Datenbankstruktur einfach zu halten.</p>
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "portfolio_items")
public class PortfolioItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    // Längenbegrenzung auf 1000 Zeichen für ausführlichere Projektbeschreibungen
    @Column(length = 1000)
    private String description;

    private String imageUrl;
    private String projectUrl;

    // Kommagetrennte Liste, z. B. "Java, Vue, AI" – bewusst kein separates Tag-Entity,
    // da die Anforderungen hier einfach sind
    private String tags; // Comma separated tags: "Java, Vue, AI"
}

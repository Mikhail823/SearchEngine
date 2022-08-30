package org.spring.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "search_lemma")
@NoArgsConstructor
public class Lemma {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String lemma;

    private int frequency;

    @Column(name = "site_id")
    private long siteId;

    public Lemma(String lemma, int frequency, long siteId) {
        this.lemma = lemma;
        this.frequency = frequency;
        this.siteId = siteId;
    }
}

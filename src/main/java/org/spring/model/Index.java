package org.spring.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "index")
public class Index {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "page_id")
    private long pageId;

    @Column(name = "lemma_id")
    private long lemmaId;

    private float ranking;

    public Index(){}

    public Index(long pageId, long lemmaId, float ranking) {
        this.pageId = pageId;
        this.lemmaId = lemmaId;
        this.ranking = ranking;
    }
}

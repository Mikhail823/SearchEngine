package org.spring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.persistence.Index;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "page",
        indexes = {@Index(name = "Path_INDX", columnList = "path")})
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String path;

    private int code;

    @Type(type = "text")
    private String content;

    @Column(name = "site_id")
    private int siteId;

}

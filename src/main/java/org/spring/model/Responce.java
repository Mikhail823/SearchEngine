package org.spring.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Responce {

    private String uri;
    private String title;
    private String snippet;
    private double relevance;
}

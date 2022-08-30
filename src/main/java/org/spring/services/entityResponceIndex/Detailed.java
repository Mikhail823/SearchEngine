package org.spring.services.entityResponceIndex;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.spring.model.Status;

@Data
@AllArgsConstructor
public class Detailed {
    String url;
    String name;
    Status status;
    long statusTime;
    String error;
    long pages;
    long lemmas;
}

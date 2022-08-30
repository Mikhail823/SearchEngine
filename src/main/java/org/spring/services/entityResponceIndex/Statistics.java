package org.spring.services.entityResponceIndex;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Statistics {
    Total total;
    Detailed[] detailed;
}

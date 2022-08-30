package org.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.spring.services.repo.StatisticService;
import org.spring.services.responces.StatisticResponseService;

@Controller
public class StatisticController {

    private final StatisticService statistic;
    @Autowired
    public StatisticController(StatisticService statistic) {
        this.statistic = statistic;
    }

    @GetMapping("/statistics")
    public ResponseEntity<Object> getStatistics(){
        StatisticResponseService stat = statistic.getStatistic();
        return ResponseEntity.ok (stat);
    }
}

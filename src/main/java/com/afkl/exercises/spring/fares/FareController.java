package com.afkl.exercises.spring.fares;

import com.afkl.exercises.spring.locations.AirportRepository;
import com.afkl.exercises.spring.locations.Location;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

import static java.math.RoundingMode.HALF_UP;
import static java.util.Locale.ENGLISH;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/fares/{origin}/{destination}")
public class FareController {

    private final AirportRepository repository;

    @Autowired
    public FareController(AirportRepository repository) {
        this.repository = repository;
    }

  
    
}

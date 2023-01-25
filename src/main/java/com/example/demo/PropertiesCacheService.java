package com.example.demo;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class PropertiesCacheService {
    @Autowired
    private Environment env;

    public String getProperty(String s){
        return env.getProperty(s);
    }
}

package com.example.RestService;

import com.example.DataCache.RestServiceDataCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
public class GreetingController {
    @Autowired
    private RestServiceDataCache cache;

    @RequestMapping("/")
    public String greeting() {
        return  cache.getCollection("users");
    }

    @PostMapping("/")
    public ResponseEntity<Map<String, String>> myMethod(@RequestBody HashMap<String, String> request){

        Map<String, String> response = new HashMap<>();
        response.put("errorMessage", "Error: Invalid Route");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

package com.hng.number_api.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@RequestMapping("/api")
public class NumberController {

    @GetMapping("/classify-number")
    public ResponseEntity<Map<String, Object>> classifyNumber(@RequestParam(value = "number", defaultValue = "28") String number) {
        Map<String, Object> response = new HashMap<>();

        // Validate input to ensure it's a valid positive integer (natural number)
        if (!number.matches("\\d+")) { // Only allows positive integers (natural numbers)
            response.put("error", true);
            response.put("message", "Invalid input. Please enter a natural number (positive whole number).");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        int num = Integer.parseInt(number);

        // Prepare the response data
        response.put("number", num);
        response.put("is_prime", isPrime(num));
        response.put("is_perfect", isPerfect(num));
        response.put("digit_sum", digitSum(num));
        response.put("properties", getProperties(num));
        response.put("math_fun_fact", getMathFunFact(num));

        // Return the response with OK status (200)
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Other methods for isPrime, isPerfect, etc...
    private boolean isPrime(int num) {
        if (num < 2) return false;
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) return false;
        }
        return true;
    }

    private boolean isPerfect(int num) {
        int sum = 1;
        for (int i = 2; i <= num / 2; i++) {
            if (num % i == 0) sum += i;
        }
        return sum == num && num != 1;
    }

    private int digitSum(int num) {
        int sum = 0;
        while (num > 0) {
            sum += num % 10;
            num /= 10;
        }
        return sum;
    }

    private List<String> getProperties(int num) {
        List<String> properties = new ArrayList<>();
        properties.add(num % 2 == 0 ? "even" : "odd");
        return properties;
    }

    private String getMathFunFact(int num) {
        try {
            String apiURL = "http://numbersapi.com/" + num + "/math?json";
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> factResponse = restTemplate.getForObject(apiURL, Map.class);

            return (factResponse != null && factResponse.containsKey("text")) ? factResponse.get("text").toString() : "No math fun fact found.";
        } catch (Exception e) {
            return "Error fetching fun fact: " + e.getMessage();
        }
    }
}

package com.hng.number_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@RequestMapping("/api")
public class NumberController {

    @GetMapping("/classify-number")
    public ResponseEntity<Map<String, Object>> classifyNumber(@RequestParam String number) {
        // Use LinkedHashMap to preserve insertion order.
        Map<String, Object> response = new LinkedHashMap<>();

        // Validate input: Accepts only valid integers (including negatives)
        if (!number.matches("-?\\d+")) {
            response.put("number", number);
            response.put("error", true);
            return ResponseEntity.badRequest().body(response);
        }

        int num;
        try {
            num = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            response.put("number", number);
            response.put("error", true);
            return ResponseEntity.badRequest().body(response);
        }

        // Build response in the required order:
        // "number", "is_prime", "is_perfect", "properties", "digit_sum", "fun_fact"
        response.put("number", num);
        response.put("is_prime", isPrime(num));
        response.put("is_perfect", isPerfect(num));
        response.put("properties", getProperties(num));
        response.put("digit_sum", digitSum(num));
        response.put("fun_fact", getMathFunFact(num));

        return ResponseEntity.ok(response);
    }

    // Checks if num is prime (numbers less than 2 are not prime)
    private boolean isPrime(int num) {
        if (num < 2) return false;
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) return false;
        }
        return true;
    }

    // Checks if num is a perfect number (only defined for positive integers)
    private boolean isPerfect(int num) {
        if (num < 1) return false;
        int sum = 1;
        for (int i = 2; i <= num / 2; i++) {
            if (num % i == 0) sum += i;
        }
        return num != 1 && sum == num;
    }

    // Computes the digit sum (using the absolute value to handle negatives)
    private int digitSum(int num) {
        int sum = 0;
        int absNum = Math.abs(num);
        while (absNum > 0) {
            sum += absNum % 10;
            absNum /= 10;
        }
        return sum;
    }

    // Returns the list of properties: adds "armstrong" if applicable, then "even" or "odd"
    private List<String> getProperties(int num) {
        List<String> properties = new ArrayList<>();
        if (isArmstrong(num)) {
            properties.add("armstrong");
        }
        properties.add(num % 2 == 0 ? "even" : "odd");
        return properties;
    }

    // Checks if num is an Armstrong number (for non-negative integers)
    private boolean isArmstrong(int num) {
        if (num < 0) return false;
        int original = num;
        int digits = String.valueOf(num).length();
        int sum = 0;
        while (num > 0) {
            int d = num % 10;
            sum += Math.pow(d, digits);
            num /= 10;
        }
        return sum == original;
    }

    // Fetches a math fun fact from the NumbersAPI and returns its "text" value.
    private String getMathFunFact(int num) {
        try {
            String apiURL = "http://numbersapi.com/" + num + "/math?json";
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> factResponse = restTemplate.getForObject(apiURL, Map.class);
            return (factResponse != null && factResponse.containsKey("text"))
                    ? factResponse.get("text").toString()
                    : "No math fun fact found.";
        } catch (Exception e) {
            return "Error fetching fun fact: " + e.getMessage();
        }
    }
}

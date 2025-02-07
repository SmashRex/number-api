package com.hng.number_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import java.util.*;

@RestController
public class NumberController {

    @GetMapping("/classify-number")
    public ResponseEntity<Map<String, Object>> classifyNumber(@RequestParam(required = false) String number) {
        // Use LinkedHashMap to maintain key order
        Map<String, Object> response = new LinkedHashMap<>();

        // Ensure error responses have "error" first
        if (number == null || number.trim().isEmpty()) {
            response.put("error", true);
            response.put("number", "");
            return ResponseEntity.badRequest().body(response);
        }

        // Validate input: Accept only valid integers (including negatives)
        if (!number.matches("-?\\d+")) {
            response.put("error", true);
            response.put("number", number);
            return ResponseEntity.badRequest().body(response);
        }

        int num;
        try {
            num = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            // For numbers too large to parse as int.
            response.put("error", true);
            response.put("number", number);
            return ResponseEntity.badRequest().body(response);
        }

        // Ensure correct order: "number", "is_prime", "is_perfect", "properties", "digit_sum", "fun_fact"
        response.put("number", num);
        response.put("is_prime", isPrime(num));
        response.put("is_perfect", isPerfect(num));
        response.put("properties", getProperties(num));
        response.put("digit_sum", digitSum(num));
        response.put("fun_fact", getMathFunFact(num));

        return ResponseEntity.ok(response);
    }

    private boolean isPrime(int num) {
        if (num < 2) return false;
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) return false;
        }
        return true;
    }

    private boolean isPerfect(int num) {
        if (num < 1) return false;
        int sum = 1;
        for (int i = 2; i <= num / 2; i++) {
            if (num % i == 0) sum += i;
        }
        return num != 1 && sum == num;
    }

    private int digitSum(int num) {
        int sum = 0;
        int absNum = Math.abs(num);
        while (absNum > 0) {
            sum += absNum % 10;
            absNum /= 10;
        }
        return sum;
    }

    private List<String> getProperties(int num) {
        List<String> properties = new ArrayList<>();
        if (isArmstrong(num)) {
            properties.add("armstrong");
        }
        properties.add(num % 2 == 0 ? "even" : "odd");
        return properties;
    }

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

    private String getMathFunFact(int num) {
        try {
            String apiURL = "http://numbersapi.com/" + num + "/math?json";
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setConnectTimeout(500);
            requestFactory.setReadTimeout(500);
            RestTemplate restTemplate = new RestTemplate(requestFactory);
            Map<String, Object> factResponse = restTemplate.getForObject(apiURL, Map.class);
            return (factResponse != null && factResponse.containsKey("text"))
                    ? factResponse.get("text").toString()
                    : "No math fun fact found.";
        } catch (Exception e) {
            return "Error fetching fun fact: " + e.getMessage();
        }
    }
}

package com.hng.number_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@RequestMapping("/api")
public class NumberController {

    // The main endpoint: If "number" is provided, process it;
    // if null or empty, redirect to the error page.
    @GetMapping("/classify-number")
    public ResponseEntity<Map<String, Object>> classifyNumber(@RequestParam(required = false) String number) {
        // Check if the "number" parameter is missing or empty
        if (number == null || number.trim().isEmpty()) {
            // Redirect (HTTP 302) to the error endpoint
            return ResponseEntity.status(302).header("Location", "/api/error").build();
        }

        // Validate: Accept only valid integers (including negatives)
        if (!number.matches("-?\\d+")) {
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("number", number);
            errorResponse.put("error", true);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        int num;
        try {
            num = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            // If the number is too large to parse as an int, return an error.
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("number", number);
            errorResponse.put("error", true);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Build the response in the required key order using a LinkedHashMap.
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("number", num);
        response.put("is_prime", isPrime(num));
        response.put("is_perfect", isPerfect(num));
        response.put("digit_sum", digitSum(num));
        response.put("properties", getProperties(num));
        response.put("math_fun_fact", getMathFunFact(num));

        return ResponseEntity.ok(response);
    }

    // Error endpoint: Returns an error JSON response when no number is provided.
    @GetMapping("/error")
    public ResponseEntity<Map<String, Object>> errorPage() {
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        // Note: The error JSON here follows the assignment's required format.
        errorResponse.put("error", true);
        errorResponse.put("message", "Missing or empty number parameter");
        return ResponseEntity.badRequest().body(errorResponse);
    }

    // --- Helper Methods ---

    // Checks if the number is prime (numbers less than 2 are not prime).
    private boolean isPrime(int num) {
        if (num < 2) return false;
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) return false;
        }
        return true;
    }

    // Checks if the number is perfect (only defined for positive numbers).
    private boolean isPerfect(int num) {
        if (num < 1) return false;
        int sum = 1;
        for (int i = 2; i <= num / 2; i++) {
            if (num % i == 0) sum += i;
        }
        return num != 1 && sum == num;
    }

    // Calculates the sum of digits (works for negative numbers by summing the absolute digits).
    private int digitSum(int num) {
        int sum = 0;
        num = Math.abs(num);
        while (num > 0) {
            sum += num % 10;
            num /= 10;
        }
        return sum;
    }

    // Determines properties: adds "even"/"odd" and "armstrong" if applicable.
    private List<String> getProperties(int num) {
        List<String> properties = new ArrayList<>();
        if (isArmstrong(num)) {
            properties.add("armstrong");
        }
        properties.add(num % 2 == 0 ? "even" : "odd");
        return properties;
    }

    // Checks if the number is an Armstrong number (only for non-negative numbers).
    private boolean isArmstrong(int num) {
        if (num < 0) return false;
        int digits = String.valueOf(num).length();
        int sum = 0;
        int temp = num;
        while (temp > 0) {
            int digit = temp % 10;
            sum += Math.pow(digit, digits);
            temp /= 10;
        }
        return sum == num;
    }

    // Fetches a math fun fact from the NumbersAPI.
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

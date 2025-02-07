package com.hng.number_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@RequestMapping("/api")
public class NumberController {

    // Endpoint now accepts the number as a path variable.
    @GetMapping("/classify-number/{number}")
    public ResponseEntity<Map<String, Object>> classifyNumber(@PathVariable String number) {
        // Use a LinkedHashMap to preserve insertion order in the JSON response.
        Map<String, Object> response = new LinkedHashMap<>();

        // Validate Input: Accept only valid integers (including negatives).
        // The regex "-?\\d+" accepts an optional negative sign followed by one or more digits.
        if (!number.matches("-?\\d+")) {
            response.put("number", number);
            response.put("error", true);
            return ResponseEntity.badRequest().body(response);
        }

        int num;
        try {
            num = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            // This catches numbers that exceed the range of int.
            response.put("number", number);
            response.put("error", true);
            return ResponseEntity.badRequest().body(response);
        }

        // Build the JSON response in the required order.
        response.put("number", num);
        response.put("is_prime", isPrime(num));
        response.put("is_perfect", isPerfect(num));
        response.put("digit_sum", digitSum(num));
        response.put("properties", getProperties(num));
        response.put("math_fun_fact", getMathFunFact(num));

        return ResponseEntity.ok(response);
    }

    // Returns true if num is prime; numbers less than 2 are not prime.
    private boolean isPrime(int num) {
        if (num < 2) return false;
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) return false;
        }
        return true;
    }

    // Returns true if num is perfect; perfect numbers are defined only for positive integers.
    private boolean isPerfect(int num) {
        if (num < 1) return false;
        int sum = 1;
        for (int i = 2; i <= num / 2; i++) {
            if (num % i == 0) sum += i;
        }
        return num != 1 && sum == num;
    }

    // Returns the sum of the digits of num (using absolute value for negative numbers).
    private int digitSum(int num) {
        int sum = 0;
        num = Math.abs(num);
        while (num > 0) {
            sum += num % 10;
            num /= 10;
        }
        return sum;
    }

    // Returns a list of properties. Currently, it checks whether the number is even/odd,
    // and if it is an Armstrong number (only for non-negative numbers).
    private List<String> getProperties(int num) {
        List<String> properties = new ArrayList<>();
        if (isArmstrong(num)) {
            properties.add("armstrong");
        }
        properties.add(num % 2 == 0 ? "even" : "odd");
        return properties;
    }

    // Returns true if num is an Armstrong number. (Armstrong numbers are defined for non-negative integers.)
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

    // Fetches a math fun fact for the given number from NumbersAPI.
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

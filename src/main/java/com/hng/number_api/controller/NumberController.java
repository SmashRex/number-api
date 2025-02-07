package com.hng.number_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
public class NumberController {

    @GetMapping("/classify-number")
    public ResponseEntity<Map<String, Object>> classifyNumber(@RequestParam(required = false) String number) {
        Map<String, Object> response = new LinkedHashMap<>();

        if (number == null || number.trim().isEmpty()) {
            response.put("error", true);
            response.put("number", "");
            return ResponseEntity.badRequest().body(response);
        }

        if (!number.matches("-?\\d+")) {
            response.put("error", true);
            response.put("number", number);
            return ResponseEntity.badRequest().body(response);
        }

        int num;
        try {
            num = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            response.put("error", true);
            response.put("number", number);
            return ResponseEntity.badRequest().body(response);
        }

        response.put("number", num);
        response.put("is_prime", isPrime(num));
        response.put("is_perfect", isPerfect(num));
        response.put("properties", getProperties(num));
        response.put("digit_sum", digitSum(num));
        response.put("fun_fact", CachedFunFact.getFunFact(num));

        return ResponseEntity.ok(response);
    }

    private boolean isPrime(int num) {
        if (num < 2) return false;
        if (num % 2 == 0 && num != 2) return false;
        for (int i = 3; i * i <= num; i += 2) {
            if (num % i == 0) return false;
        }
        return true;
    }

    private boolean isPerfect(int num) {
        if (num <= 1) return false; // Fix: 1 is NOT a perfect number
        int sum = 1;
        for (int i = 2; i * i <= num; i++) {
            if (num % i == 0) {
                sum += i;
                if (i != num / i) sum += num / i;
            }
        }
        return sum == num;
    }

    private int digitSum(int num) {
        num = Math.abs(num);
        return String.valueOf(num).chars().map(c -> c - '0').sum();
    }

    private List<String> getProperties(int num) {
        List<String> properties = new ArrayList<>();
        if (num >= 0 && isArmstrong(num)) {
            properties.add("armstrong");
        }
        properties.add(num % 2 == 0 ? "even" : "odd");
        return properties;
    }

    private boolean isArmstrong(int num) {
        if (num < 0) return false; // Fix: Negative numbers cannot be Armstrong numbers
        int original = num, sum = 0, digits = String.valueOf(num).length();
        while (num > 0) {
            sum += Math.pow(num % 10, digits);
            num /= 10;
        }
        return sum == original;
    }
}

class CachedFunFact {
    private static final Map<Integer, String> cache = new HashMap<>();
    private static final RestTemplate restTemplate = new RestTemplate();

    static String getFunFact(int num) {
        if (cache.containsKey(num)) return cache.get(num);
        try {
            String url = "http://numbersapi.com/" + num + "/math?json";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            String fact = (response != null && response.containsKey("text")) ? response.get("text").toString() : "No fun fact found.";
            cache.put(num, fact);
            return fact;
        } catch (Exception e) {
            return "No fun fact found.";
        }
    }
}

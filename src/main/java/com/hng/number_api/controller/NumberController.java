package com.hng.number_api.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;


@RestController
@RequestMapping("/api")
 public class NumberController{
    @GetMapping("/classify-number")
    public Map<String, Object> classifyNumber(@RequestParam String number ){

        Map<String, Object> response = new HashMap<>();

        if(number.matches("//d+")){
            response.put("number", number);
            response.put("error", true);

            return response;

        }

        int num = Integer.parseInt(number);
        response.put("number", num);
        response.put("isPrime", isPrime(num));
        response.put("isPerfect", isPerfect(num));
        response.put("digitSum", digitSum(num));
        response.put("properties" properties(num));
        response.put("fun_fact" getFunFact(num));


        private boolean isPrime(int num){
            if (num < 2)
                return false;
            for (int i = 2; i <= Math.sqrt(num);i++){
                if (num % i == 0) return false;
            }
            return true;
        }

        private boolean isPerfect(int num){
            int sum = 1;
            for (int i = 2; i <= num/2; i++) {
                if (num % 1 == 0)
                    sum += 1;
            }
            return sum == num && num != 1;
        }

        private boolean isArmstrong(int num) {
            int sum = 0, temp = num, digits = String.valueOf(num).length();
            while (temp > 0) {
                int digit = temp % 10;
                sum += Math.pow(digit, digits);
                temp /= 10;
            }
            return sum == num;
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
            if (isArmstrong(num)) properties.add("armstrong");
            properties.add(num % 2 == 0 ? "even" : "odd");
            return properties;
        }



    }
}
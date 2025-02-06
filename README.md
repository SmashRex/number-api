# Number Classification API

## Overview
This API allows users to classify a number based on its mathematical properties, such as:
- Checking if a number is **prime**
- Checking if a number is **perfect**
- Checking if a number is **Armstrong**
- Returning the **sum of its digits**
- Determining whether it is **odd or even**
- Fetching a **fun math fact** about the number from [NumbersAPI](http://numbersapi.com/)

## Features
- Validates input to ensure only **natural numbers** (positive integers) are accepted.
- Returns multiple properties of the number.
- Fetches a **math fun fact** using an external API.
- Supports **CORS** for cross-origin requests.

## API Specification

### **1. Classify a Number**
**Endpoint:**
```http
GET /api/classify-number?number={number}
```


### Response Example (Valid Input - 28) ###
```json
{
    "number": 28,
    "is_prime": false,
    "is_perfect": true,
    "digit_sum": 10,
    "properties": ["even"],
    "math_fun_fact": "28 is the second perfect number."
}
```

### Response Example (Invalid Input - -5) ###

```json

{
"number": "-5",
"error": true,
"message": "Invalid input. Please enter a natural number (positive whole number)."
}

```

### Installation & Running Locally ###
1. Clone the Repository
```git
   git clone https://github.com/SmashRex/number-api.git
   cd number-api
   ```
2. Build & Run
   Ensure you have Java 17+ and Maven installed.
```java

./mvnw spring-boot:run;
```
The API will start at http://localhost:8080/api/classify-number?number=28

3. Deploy to Railway
   

### Technologies Used ###
1. Spring Boot (Java)
2. Numbers API (External Math Facts API)
3. Maven (Build Tool)
4. Railway (Deployment)

### Test Here ###
https://number-api-production.up.railway.app/
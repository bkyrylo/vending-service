
====
docker compose up
Open http://localhost:5050/browser/
Set Master Password
Add new server with Connection host: host.docker.internal
add Password: Password, click save
===
./gradlew bootRun


curl http://localhost:8080/dummy -u "buyer:buyerpassword" -v (if no SSL)
curl http://localhost:8080/dummy -H "Authorization: Basic base64string" -v (if no SSL)

keytool -genkeypair -alias springboot -keyalg RSA -keysize 4096 -storetype PKCS12 -keystore springboot.p12 -validity 3650 -storepass password
curl -k https://localhost:8443/dummy -u "buyer:buyerpassword" -v (with SSL)
curl --insecure https://localhost:8443/dummy -u "buyer:buyerpassword" -v (with SSL)


curl -k -X POST -H "Content-Type: application/json" -d '{"username": "test13", "password": "testpass1", "roles": ["seller", "buyer"]}' https://localhost:8443/user -v

curl -k -X POST -H "Content-Type: application/json" -d '{"productName": "Laser", "cost": 10, "amountAvailable": 11}' https://localhost:8443/product -u "test13:testpass1" -v

curl -k -X GET  https://localhost:8443/product/f8edceac-6733-4263-9811-3628b35d9fee -u "test13:testpass1" -v

======

Create users
curl -k -X POST -H "Content-Type: application/json" -d '{"username": "test13", "password": "testpass1", "roles": ["seller", "buyer"]}' https://localhost:8443/user -v
curl -k -X POST -H "Content-Type: application/json" -d '{"username": "test15", "password": "testpass1", "roles": ["buyer"]}' https://localhost:8443/user -v

Create product
curl -k -X POST -H "Content-Type: application/json" -d '{"productName": "Laser", "cost": 10, "amountAvailable": 11}' https://localhost:8443/product -u "test13:testpass1" -v

Deposit user
curl -k -X POST -H "Content-Type: application/json" -d '{"depositInCents": 10}' https://localhost:8443/user/deposit -u "test15:testpass1" -v

Buy product
curl -k -X POST -H "Content-Type: application/json" -d '{"amount": 10}' https://localhost:8443/product/{id}/buy -u "test15:testpass1" -v

Delete the user
curl -k -X DELETE  https://localhost:8443/user -u "test13:testpass1" -v

================================
================================


# CODING CHALLENGE


# Exercise brief

Design an API for a vending machine, allowing users with a “seller” role to add, update or remove products, while users with a “buyer” role can deposit coins into the machine and make purchases. Your vending machine should only accept 5, 10, 20, 50 and 100 cent coins

**Tasks**

- REST API should be implemented consuming and producing “application/json”
- Implement product model with amountAvailable, cost (should be in multiples of 5), productName and sellerId fields
- Implement user model with username, password, deposit and role fields
- Implement an authentication method (basic, oAuth, JWT or something else, the choice is yours)
- All of the endpoints should be authenticated unless stated otherwise
- Implement CRUD for users (POST /user should not require authentication to allow new user registration)
- Implement CRUD for a product model (GET can be called by anyone, while POST, PUT and DELETE can be called only by the seller user who created the product)
- Implement /deposit endpoint so users with a “buyer” role can deposit only 5, 10, 20, 50 and 100 cent coins into their vending machine account (one coin at the time)
- Implement /buy endpoint (accepts productId, amount of products) so users with a “buyer” role can buy a product (shouldn't be able to buy multiple different products at the same time) with the money they’ve deposited. API should return total they’ve spent, the product they’ve purchased and their change if there’s any (in an array of 5, 10, 20, 50 and 100 cent coins)
- Implement /reset endpoint so users with a “buyer” role can reset their deposit back to 0
- Take time to think about possible edge cases and access issues that should be solved

**Evaluation criteria:**

- Language/Framework of choice best practices
- Edge cases covered
- Write tests for /deposit, /buy and one CRUD endpoint of your choice
- Code readability and optimization

**Bonus:**

- If somebody is already logged in with the same credentials, the user should be given a message "There is already an active session using your account". In this case the user should be able to terminate all the active sessions on their account via an endpoint i.e. /logout/all
- Attention to security

## Deliverables

A Github repository with public access. Please have the solution running and a Postman / Swagger collection ready on your computer so the domain expert can tell you which tests to run on the API.


### Will this code be shown to the client?

Assume yes. Should also help us in reducing the time by clients evaluating profiles.

================================
================================


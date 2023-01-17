NOTES:

Locally service could be run in IDE after the containers, declared in docker-compose.yml, has been started. One of the containers is pgadmin,
which allow to quickly check db tables modified.

To run db and db admin containers:
`docker compose up`

To use pgadmin (optionally):
- open `http://localhost:5050/browser/`
- set Master Password
- add new server with Connection host: `host.docker.internal`

Service run on SSL port 8443, as it is recommended for authentication type BASIC. Examples of curl commands see below:
- create users

curl -k -X POST -H "Content-Type: application/json" -d '{"username": "test13", "password": "testpass1", "roles": ["seller", "buyer"]}' https://localhost:8443/user -v
curl -k -X POST -H "Content-Type: application/json" -d '{"username": "test15", "password": "testpass1", "roles": ["buyer"]}' https://localhost:8443/user -v

- create product

curl -k -X POST -H "Content-Type: application/json" -d '{"productName": "Laser", "cost": 10, "amountAvailable": 11}' https://localhost:8443/product -u "test13:testpass1" -v

- deposit user

curl -k -X POST -H "Content-Type: application/json" -d '{"depositInCents": 10}' https://localhost:8443/user/deposit -u "test15:testpass1" -v

- buy product

curl -k -X POST -H "Content-Type: application/json" -d '{"amount": 10}' https://localhost:8443/product/{id}/buy -u "test15:testpass1" -v

- delete the user

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


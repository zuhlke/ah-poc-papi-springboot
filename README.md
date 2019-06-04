# ah-poc-papi-springboot
Aimless-Hammer PAPI Springboot implementation

[![Build Status](https://travis-ci.com/zuhlke/ah-poc-papi-springboot.svg?branch=master)](https://travis-ci.com/zuhlke/ah-poc-papi-springboot) 

## Summary

Exposes a resource `GET /balance` resource which requires a request parameter `customer-id`. The website in the description for this repo is an example of a valid call to this PAPI.

## Run the server locally

`mvn spring-boot:run`

## Run the tests

`mvn test`

## Build a jar

`mvn package`

## Travis pipeline

- Pipeline file is `.travis.yml`.
- Runs the tests, builds a jar, deploys to PCF.
- Environment variables are injected by travis.

## Contract tests

The contract tests are defined in the submodule called `contract-tests/`. When built, this produces a program which executes contract tests against a defined endpoint.

There is a script in there called `build-contract`, which builds the contract from source.  It creates a jar using gradle, and puts it in a place where providers can find it.

- Currently that is within this repository, however it could also be some kind of file repository or cloud storage facility.

Executable files in `bin/` execute contract tests against providers deployed into the PCF space called 'development'.

- Currently 'development' is the only PCF space we deploy to at all. In a more realistic scenario, we would likely use results from the contract tests to decide whether to push to an environment
closer to production. That kind of deployment behaviour is currently out of scope in this PoC.

## Example

If you hit this API with a request like

`GET localhost:8080/reactive-balance?customer-id=10101010`

then you'll get the response body below:

```
[
    {
        "accountType": "CreditCardAccount",
        "accountNumber": "1234567890",
        "balance": "1234.50"
    },
    {
        "accountType": "CurrentAccount",
        "accountNumber": "64746383648",
        "balance": "34.50"
    }
]
```
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

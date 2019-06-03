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

This PAPI is dependent on the ah-poc-sapi-cc-bal SAPI, so to get automatic feedback about the integration of these APIs, the PAPI defines an executable contract against the SAPI. This contract is
run against the SAPI in the SAPI's pipeline. The contract test artifact, a jar, is committed to source control so that it is accessible by the providers to this PAPI, so they can access it for use
in their pipelines.

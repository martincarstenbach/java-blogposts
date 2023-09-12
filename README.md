# Java Blogpost Repository

A collection of code from my Java related blogposts at https://martincarstenbach.wordpress.com/

These posts are intended to complement the articles and should be easier to maintain. Nevertheless these code samples aren't what I do for a living, and there is a real chance the examples are getting a little long in the tooth. Please ensure you check the POM for outdated dependencies and bump versions as needed. 

The following examples are currently contained in this repository

## Secure External Password Store

This example shows how to use the Secure External Password Store together with Maven to write a simple database application. The corresponding blog post can be found at [https://martincarstenbach.wordpress.com/2020/07/20/jdbc-the-oracle-database-using-maven-central/](https://martincarstenbach.wordpress.com/2020/07/20/jdbc-the-oracle-database-using-maven-central/)

**Requirements**

- the Secure External Password Store must be in place before this code can be executed, have a look at the `generate_wallet.sh` script in case you'd like to create the wallet
- the application uses a properties file, please ensure you update the only entry to match your environment!

## Transparent Application Failover Demo

A short demonstration on why you need the Oracle Call Interface Driver (OCI) when writing Java code making use of Transparent Application Failover. The corresponding blog post was written in 2020, updated 07/2021 and can be found at [https://martincarstenbach.wordpress.com/2020/08/18/jdbc-the-oracle-database-if-you-want-transparent-application-failover-you-need-the-oci-driver/](https://martincarstenbach.wordpress.com/2020/08/18/jdbc-the-oracle-database-if-you-want-transparent-application-failover-you-need-the-oci-driver/)

This demo also requires setting up the secure external password store. Details are provided in the blog post covering the Maven example.

**Requirements**

- the Secure External Password Store must be in place before this code can be executed, have a look at the `generate_wallet.sh` script in case you'd like to create the wallet
- the application uses a properties file, please ensure you update the only entry to match your environment!

## SpringBoot Thymeleaf Demo

A tiny application using SpringBoot JDBC and `SimpleJdbcCall` to execute a function in a package. This example has been added because it isn't always clear from other examples found via search engines how to invoke PL/SQL functions. Using Oracle 19c, Universal Connection Pool (UCP), Thymeleaf, and Bootstrap.

**Requirements**

- the Secure External Password Store must be placed in `src/database/wallet` before this code can be executed, have a look at the `generate_wallet.sh` script in case you'd like to create the wallet
- the application uses a properties file, please ensure you update the only entry to match your environment!

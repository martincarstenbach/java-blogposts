# Springboot Thymeleaf Demo

A very small application showcasing the use of Spring Data JDBC with Oracle Database 19c and its Universal Connection Pool. Rather than providing an insecure configuration this example relies on an Oracle wallet, aka _Secure External Password Store_.  This way no username/password combinations are stored in Git.

A future example will demonstrate the use of JPA/Hibernate with Oracle Database and Spring Boot 3.1.x. 

Components used to create this demo application

- Spring Boot 3.1.3 via <https://start.spring.io> (more details are in `pom.xml`)
- Oracle Database 19c
- Oracle JDBC drivers 19.19.0.0 production
- JDK 17.0.8 on Linux x86-64

## Database Setup

All you need to run this application is a database account with certain privileges, see `src/database/setup_user.sys` 
for an example. Once the account has been created, use Liquibase (built into `sqlcl`) to deploy the changelog.

## References

- [Developers Guide For Oracle JDBC 21c on Maven Central](https://www.oracle.com/database/technologies/maven-central-guide.html)
- [Oracle Database sample schemas](https://github.com/oracle-samples/db-sample-schemas)
- [Universal Connection Pool](https://docs.oracle.com/en/database/oracle/oracle-database/19/jjucp/index.html#Oracle%C2%AE) 19c
- [Documentation of all UCP settings](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html) in `application.properties`
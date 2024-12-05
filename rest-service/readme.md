# SpringBoot, JobRunr and Oracle Database Free

This is a sample application linking [SpringBoot](https://spring.io/projects/spring-boot) 3.4.0, [JobRunr](https://www.jobrunr.io/en/) 7.3.2 and [Oracle Database 23ai Free](https://www.oracle.com/database/free/).

Oracle Database 23ai is the latest and greatest release of the Oracle Database, offering many new productivity features to developers. For quite some time now the database can be powered by a container engine, podman and docker are probably the most common ones. Rather than spending time installing and configuring the database, all that is needed is a `podman run`.

## Overview

The application requires an Oracle Database 23ai database to connect to. It was written using IntelliJ community edition, and this is the expected environment for building and compiling. You may of course adapt to your needs!

### Database

You can use the following compose file, tested with `docker compose` as well as `podman-compose` to get the database:

```yaml
version: "3"
# THIS IS STRICTLY A LAB SETUP ONLY
services:
    dbfree:
        image: docker.io/gvenzl/oracle-free:23.5-faststart
        ports:
            - 1521:1521
        environment:
            - ORACLE_PASSWORD=replaceWithYourPassword
            - APP_USER=replaceWithYourUsername
            - APP_USER_PASSWORD=replaceWithYourOtherPassword
        volumes:
            - oradata-vol:/opt/oracle/oradata
        networks:
            - backend

volumes:
    oradata-vol:
networks:
    backend:
```

You can learn more about the image and how to use it on [Docker Hub](https://hub.docker.com/r/gvenzl/oracle-free).

Bring the database up using `podman-compose up -d` and wait a few seconds for it to be accessible. You can tail the logs using `podman logs`, once you see

```
#########################
DATABASE IS READY TO USE!
#########################
```

You are good to go! You have access to administrative accounts (SYS, SYSTEM) as well as your application user. Make sure to update `application.properties` to reflect your settings.

### Deploying the initial schema objects

The initial table and stored procedure are provided in `src/database/`. You can use your favourite Liquibase client - for example Oracle's [SQLcl](https://www.oracle.com/database/sqldeveloper/technologies/sqlcl/) to deploy the intial code.

Here's an example of a deployment triggered by SQLcl:

```
SQL> lb update -changelog-file controller.xml
--Starting Liquibase at 2024-12-05T08:41:03.120770597 (version 4.25.0.305.0400 #0 built at 2024-10-31 21:25+0000)
Running Changeset: sequence/s_job_log_table_sequence.xml::1c0c648b705b3b081d091dff41b360a75cac1e8a::(MARTIN)-Generated

Sequence "S_JOB_LOG_TABLE" created.

Running Changeset: table/job_log_table_table.xml::ca983d6b3664ae5290698e1381cf67deb3d66ebb::(MARTIN)-Generated

Table "JOB_LOG_TABLE" created.

Running Changeset: package_spec/app_package_package_spec.xml::490dc0ba5b9b86d38d13aab44df175732250eda6::(MARTIN)-Generated

Package APP_PACKAGE compiled

Running Changeset: package_body/app_package_package_body.xml::653ebc34923f60524dcf5b2773fe1c89e1dddb8a::(MARTIN)-Generated

Package Body APP_PACKAGE compiled

UPDATE SUMMARY
Run:                          4
Previously run:               0
Filtered out:                 0
-------------------------------
Total change sets:            4

Liquibase: Update has been successful. Rows affected: 4

Operation completed successfully.
```

## Application

The application has been written using IntelliJ IDEA and is based on JDK 21. It uses `ojdbc11-production` as described in the [Developers Guide For Oracle JDBC on Maven Central](https://www.oracle.com/database/technologies/maven-central-guide.html). This way you can be sure that Oracle's Universal Connection Pool (UCP) is available. Should you want to connect to a TLS-protected listener (think Autonomous Database) you're ready to go, too.

Before you can use it, set a number of environment variables in `.env`:

- USERNAME: the name of the application user provided in the compose file earlier
- PASSWORD: the matching password

The _build and run_ configuration should have been set to pick environment variables up from this file, but in case it doesn't, add them.

Start the application like normal, once it's up, you can submit jobs like so:

```shell
curl -vi -H "Content-Type: application/json" \
-d '{ "jobName": "testjob", "requestedBy": "martin" }' \
http://localhost:8080/job
```

The REST endpoint accepts the request and schedules the job, as defined by the Service. Note that only `testjob` will be run, everything else is going to throw an error. In any case, the caller gets a JSON with the results.

## Todo

> The application is nowhere near complete and mustn't be considered _production-like_. 

The following things need to be added:

- [ ] protecting the REST endpoint using OAuth2
- [ ] enable HTTPS
- [ ] add robust logging
- [ ] add unit tests
- [ ] provide better documentation
- [ ] use embedded Liquibase migration tool rather than an external one
- [ ] add Dockerfile
## Required software:

* Java 1.8+
* PostgreSQL 9+
* Apache Maven 3+

## Build:
CD to the pom folder

```
mvn clean package -DskipTests
```

The jar will be in the `/target` folder.

This SQL script has to be executed once when the server is deployed for the first time:

```
drop database if exists "gameLabz";
create database "gameLabz";
```

The required tables will be created by the application.

## Usage:

```
java -jar gameLabz-1.0-SNAPSHOT.jar [parameters]
```

Parameters:
--server.port: the server port, default: 8080
--database.username: the username for the PostgreSQL database, mandatory
--database.password: the password for the PostgreSQL database, mandatory

# Yoga
## Table of contents

- Description and goals
- Installation
- Database setup
- Run application
- Run tests
- Technologies
- Compodoc
- Screenshots

## Description and goal

Yoga application concerns unit, integration and e2e testing for the back and front end.

This is a full stack application that allow to configure Yoga classrooms and booking system.
It's just a proof of concept to work around application testing with JUnit, Jest and Cypress.

## Installation

### Clone the Project

Clone these repositories :
> git clone https://github.com/popcodelab/Yoga

### Install Dependencies

#### Back-End:

> mvn clean install

#### Front-End:

> npm install

### Configuring the Back-End

Open the `application.properties` file located in the `back/src/main/resources` directory to Replace the properties with your parameters:

```properties
spring.datasource.url=${APP_DB_URL}
spring.datasource.username=${APP_DB_USERNAME}
spring.datasource.password=${APP_DB_PASSWORD}
```

## Database setup 

Make sure that you have MySQL installed on your system.

1. Log into MySQL using the following command :
> mysql -u `<username>` -p

2. Create the database :

> CREATE DATABASE `<database_name>`;

3. Select the created database :
> USE `<database_name>`;


### Build the database

Use the SQL script located in `ressources\sql\script.sql`  to create the schema :

> SOURCE `<path_to_script.sql>`;
     

Default credential are :
- login: yoga@studio.com
- password: test!1234

## Run application

1. Frontend
   
   - In your terminal, run the command below.
    
        ```bash
        cd front
        npm run start
        ```

     The frontend will launch in your browser at `http://localhost:4200`
  
2. Backend

     - In a separate terminal, run the command below.

          ```bash
          cd back
          mvn spring-boot:run
          ```

        The backend server will launch at `http://localhost:8080`

---

## Run Tests

### Backend - Unit and integration tests

1. Run tests and generate a coverage report.

    ```bash
    cd back
    mvn clean test
    ```
    
2. The report has been generated in  `back/target/site/jacoco` directory. Open the `index.html` file in a web browser.

![Junit Coverage Report](ressources/test-reports/back.png)

### Frontend - Unit and Integration

1. Run tests and generate a coverage report.

    ```bash
    cd front
    npm run test
    ```

2. The report has been generated in `front/coverage/jest/lcov-report` directory. Open the `index.html` file in a browser.

![Jest Coverage Report](ressources/test-reports/front-jest.png)

### Frontend - End-to-End

1. Run the Cypress Test Runner.

    ```bash
    npm run e2e
    ```

2. Run the end-to-end tests and generate the coverage report.

    ```bash
    npm run e2e:coverage
    ```
3. The report has been generated in `front/coverage/lcov-report` directory. Open the `index.html` file in a browser.

![e2e Coverage Report](ressources/test-reports/front-e2e.png)

## Technologies
Front-end :  
![Static Badge](https://img.shields.io/badge/Angular-14.2.0-red)
![Static Badge](https://img.shields.io/badge/Jest-28.1.3-green)
![Static Badge](https://img.shields.io/badge/Cypress-10.11.0-blue)

Back-end :  
![Static Badge](https://img.shields.io/badge/Java-17.0.10-orange)
![Static Badge](https://img.shields.io/badge/Spring_Boot-2.6.1-green)
![Static Badge](https://img.shields.io/badge/Maven-4.0.0-purple)
![Static Badge](https://img.shields.io/badge/Junit-5.8.1-red)
![Static Badge](https://img.shields.io/badge/Jacoco-0.8.5-yellow)

<br>
<hr>

 <div align="center">

 [![forthebadge](https://forthebadge.com/images/badges/build-with-spring-boot.svg)](https://forthebadge.com)
 [![forthebadge](https://forthebadge.com/images/badges/uses-git.svg)](https://forthebadge.com)
 [![forthebadge](https://forthebadge.com/images/badges/made-with-typescript.svg)](https://forthebadge.com)
 [![forthebadge](https://forthebadge.com/images/badges/made-with-java.svg)](https://forthebadge.com)
![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)
</div>
<hr/>
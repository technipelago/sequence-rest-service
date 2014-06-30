# Sequence Generator REST Service

This Spring Boot application uses Reactor framework to generate unique sequence numbers in concurrent environments.

This service can be used to generate customer, invoice, case numbers, etc.
You can control the starting number and the format for each sequence.

In Grails applications you can use the plugin [Restful Sequence Number Generator](https://github.com/goeh/grails-sequence-generator-rest)
as a client. The Grails client will communicate with this service to get unique sequence numbers.

## Why not use database sequences?

Although database sequences are fast they put restrictions on data types (i.e. require numbers).
This service lets you use formatted sequence numbers, for example *TICKET-1234* and you can also change the sequence number programatically.
You could for example reset the sequence to start with *YYYY0001* on the first of January every year.

## Configuration

*application.properties*

    server.port=8082
    
    spring.datasource.url=jdbc:mysql://localhost/sequence_db
    spring.datasource.username=dbuser
    spring.datasource.password=dbpasswd
    spring.datasource.driverClassName=com.mysql.jdbc.Driver
    spring.jpa.hibernate.ddl-auto=update
    
    sequence.user.password=password
    sequence.admin.password=password

## Running

    ./gradlew bootRun

## Initializing

### Create a new sequence

The following request creates a new sequence called **Customer** that will start at **1001** and return numbers formatted as **01001**.

    curl --user admin:password -H "Accept: application/json" -H "Content-Type: application/json" -X POST -d '{"number":1001, "format":"%05d"}' http://localhost:8082/api/sequence/1/Customer

### Get next number

The following three requests returns number **01001**, **01002** and **01003**.

    curl -u user:password  http://localhost:8082/api/sequence/1/Customer/next
    { "number": "01001" }
    
    curl -u user:password  http://localhost:8082/api/sequence/1/Customer/next
    { "number": "01002" }
    
    curl -u user:password  http://localhost:8082/api/sequence/1/Customer/next
    { "number": "01003" }


### Get status of a sequence

The following request returns the status of the **Customer** sequence without increasing the number.

    curl -u user:password  http://localhost:8082/api/sequence/1/Customer
    {
      "timestamp" : 1404149733472,
      "name" : "Customer",
      "format" : "%05d",
      "number" : 1004
    }

### Update a sequence

The following request updates an existing sequence called **Customer** so that the next number will be **5000**.
To reduce concurrency issues you must specify the current number AND the new number.
If the current sequence number is not equal to the specified number, the update will not be applied.

    curl --user admin:password -H "Accept: application/json" -H "Content-Type: application/json" -X PUT -d '{"current":1004, "number":5000}' http://localhost:8082/api/sequence/1/Customer

    curl -u user:password  http://localhost:8082/api/sequence/1/Customer/next
    { "number": "05000" }
    
### Delete a sequence

The following request deletes an existing sequence called **Customer**.

    curl --user admin:password -H "Accept: application/json" -H "Content-Type: application/json" -X DELETE -d '{}' http://localhost:8082/api/sequence/1/Customer
    
    curl -u user:password  http://localhost:8082/api/sequence/1/Customer
    404 NOT FOUND
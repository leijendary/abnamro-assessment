# ABN AMRO Assessment
This is a simple REST API that does simple recipe CRUD operations based on the following requirements: 
1. It must be a REST application implemented using Java (use a framework of your choice). ✅
2. Your code should be production-ready. ✅
3. REST API must be documented. ✅
4. Data must be persisted in a database. ✅
5. Unit tests must be present. ✅
6. Integration tests must be present. ✅

## Running the microservice
There are two different ways that I could recommend you to run this microservice:

### Docker Compose
```shell
docker comose up
```

### IntelliJ
Copy `.example.env` to `.env` and paste the following content:
```dotenv
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/abnamro_assessment
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=12345678
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
# Yes, it's fine for me to give the follow links, including 
# some credentials that I will show later in this README.
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=https://leijendary.us.auth0.com/
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI=https://leijendary.us.auth0.com/.well-known/jwks.json
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_AUDIENCES=http://localhost:8000
```
Add the `.env` file to IntelliJ's run configuration.

<br>
Or, if you really want to be adventurous:
<br>

### Gradle
*Assuming the environment variables from above are already set*
```shell
./gradlew bootRun
```

## Technology Stack
- Spring Boot 3
- Spring Actuator
- Spring Data JPA
- Spring Data Redis
- Spring OAuth2 Resource Server
- Spring Validation
- Spring Web
- Spring OAuth2 jose (for JWTs)
- Spring Test
- Liquibase
- PostgreSQL
- Lombok (this is a must)
- MapStruct (for Entity <---> DTO mapping)
- JavaFaker
- OpenAPI
- Docker

## OpenAPI
After running the application, just go to `http://localhost:8000/swagger-ui.html` to view the OpenAPI documentation.

To log in, follow the API flow below.

## API Flow
To have an easier process, a postman collection is provided under `.postman/collection.json`. 

Import the collection in to your postman app and then run the requests as you like.
Credentials are already saved there for you.

You have to log in first before accessing the other APIs.

You could also use a cURL command to get the bearer token:
```shell
curl --location --request POST 'https://leijendary.us.auth0.com/oauth/token' \
   --header 'Authorization: Basic amppdnpDZkNnRTZ6c2RxY0FyN2twRHVybU9UNFpjV2M6eUs1LXVjYzQtV0paMkwxNW5FZUloMzY2RlVkOVNVTnYzbTUtdkFnN3JqY29ScjhGR0ZqQmExMF9Kd3dqMEh2eQ==' \
   --header 'Content-Type: application/x-www-form-urlencoded' \
   --data-urlencode 'username=abnamro@leijendary.com' \
   --data-urlencode 'password=ABNAMRO1234!' \
   --data-urlencode 'grant_type=password' \
   --data-urlencode 'audience=http://localhost:8000' \
   --data-urlencode 'scope=urn:recipe:page:v1 urn:recipe:create:v1 urn:recipe:get:v1 urn:recipe:update:v1 urn:recipe:delete:v1'
```

Get the `access_token` field from the response and use that value to authorize the next requests.

In summary, if you want to create a recipe, do the following:
1. Call the login API (provided above).
2. Get the `access_token` response field.
3. Create a request to make a recipe (use the json object from swagger or postman).
4. Add the `access_token` to the `Authorization` header. Example: `Authorization: Bearer ${access_token}`
5. Send the request.

### API Testing
I included the credentials in the postman collection for the sake of your easy testing. 

*I will remove the credentials from my Auth0 account later*

## Folder Structure
```
abnamro-assessment
└───.github
│   └───workflows
│       │   push-main.yaml <- github workflow for pushing to the main branch.
│       │   test.yaml <- the whole test workflow.
│
└───.postman
│   │   collection.json <- import this to postman.
│
└───src
│   └───main
│   │   └───java
│   │   │   └───nl.abnamro.assessment
│   │   │   │   └───api <- the APIs (REST, gRPC, etc) and the logic.
│   │   │   │   │   └───v1 <- versioning of APIs.
│   │   │   │   │   │   └───data <- the DTOs for the request/response of the v1 endpoints.
│   │   │   │   │   │   └───mapper <- the logic on how to map DTOs to entities and vice versa.
│   │   │   │   │   │   └───rest <- the REST APIs.
│   │   │   │   │   │   └───service <- the business logic for the v1 API endpoint.
│   │   │   │   │
│   │   │   │   └───core <- the generic microservice backbone and core.
│   │   │   │   └───model <- the database entities.
│   │   │   │   └───repository <- the data access layer, to communicate with the database.
│   │   │   │   └───specification <- JPA specification logic on how to filter the data. (where clauses).
│   │   │   │
│   │   │   │   Application.java <- starting point of the service.
│   │
│   └───test <- same structure as main, but for tests
│
│   .example.env <-- copy this to .env and add your environment variables.
│   .env <-- you should create this from .example.env.
│   docker-compose.yaml <- to easily run this service.
│   Dockerfile <- you know what this is.
```

The `test` folder is the same structure with the one above. But only runs tests, of course.


### Why "rest" and not "controller"?
This is an opinionated package naming convention. I preferred it to be named this way to make way for other protocols like `gRPC`.

So if ever I decided to add a `gRPC` endpoint, I could simply create a package named `grpc` and add controllers suffixed with `*GRpc` (like `RecipeGRpc`).

### v1 to v2 transition?
1. Create a `v2` package under `api`.
2. Reuse whatever you can reuse from `v1`.
3. Create separate logic, do not change anything in `v1` unless it's really needed (like security patches).
4. Never fail consumers that uses your old API.

### Why the core package?
Glad you asked.

**_Developer Experience!_**

The reason behind this is for a **"cleaner"** parent package structure. 

The developer can focus more on the business use case rather than seeing a clutter of core microservice logic that is not being changed much often.

### Why the `DataResponse` and `ErrorResponse` objects?
This is to basically standardize the JSON API response based on the HTTP status code (success or error).

There are other things I also did here like implemented some of the [JSON API specification](https://jsonapi.org/) (not all, of course. they are just too much and unnecessary) like meta and links.

I also changed the `Page` object response from having too many fields to just the important fields needed for paginating.

### Tests
My tests are mostly integration and unit tests and not mocks because I really want to see the end to end flow working, not just some mocks that I did.

JSON paths are used so that I could validate that the JSON schemas are correctly returned even before being mapped to the class equivalent.

## General Architecture
**Spring OAuth2 Resource Server** is added to this service to show that I also implement best practices when it comes to security.

I would, however, prefer that these types of things are implemented in an API Gateway layer or service mesh layer (like [Istio](https://istio.io/)) or any infrastructure-level configuration for a number of reasons.

## Wrap up
Hopefully the codebase that I showed here are up to your standards. 🙏

Thank you so much for letting me have this assessment, and also thank you for your time!
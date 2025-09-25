# Goat Notes Ktor

A Kotlin Ktor backend for note-taking, using DynamoDB and AWS Cognito/Keycloak for authentication.
For now, we use Keycloak for local dev and Cognito for production. This can be changed.

---

## Prerequisites

- Docker
- Java 21+

---

## Local Development Setup

1. Create shared keycloak data with:
```bash
docker volume create goat-notes-dynamodb-data && docker volume create shared-keycloak-data
```
This makes it so that the data in DynamoDB Local and Keycloak persists between container restarts, and we can share the Keycloak data between containers. E.g. the equivalent of this app that is using Spring Boot, or if we want to keep the same keycloak settings for multiple projects.

2. **Spin up containers**

   ```bash
   docker compose up -d
   ```

   This starts DynamoDB Local and Keycloak containers.


3. **Create DynamoDB Table**

   Wait until DynamoDB is running, then execute:

   ```bash
   sh scripts/create-table.sh
   ```
    This is a one-time setup, we persist the data in our containers.


4. **Configure Keycloak**

    - Set up realms, clients, and users as needed. This is a one-time setup, we persist the data in our containers.


5. **Run the Application**

   ```bash
   ./gradlew run --args="-config=application.conf -config=application-dev.conf"
   ```
   Or set it up with run configurations in IntelliJ and press the Play button.

---

## Production/Cloud Deployment

1. **Provision DynamoDB and AWS Cognito**

    - Use AWS Console or CLI to create the required table.
    - Set up clients, and users as needed.


2. **Environment Variables**

   Set the following environment variables (e.g., in a `.env` file):

   ```
   JWT_ISSUER=<your jwt issuer>
   JWT_JWKS_URL=<your jwt jwks url>
   AWS_PROFILE_NAME=<your aws profile name>
   AWS_DYNAMODB_REGION=<your dynamodb region>
   ```


3. **Run the Application**

   ```bash
   ./gradlew run --args="-Denv=prod"
   ```

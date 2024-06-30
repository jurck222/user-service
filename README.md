# Users service
## Description
Microservice that is a part of AppointmentManager app. Its job is to atuhenticate users on login and fetch user data when needed.

## Api endpoints
- Register user:
  - path: /api/v1/auth/register
  - method: POST
  - description: receives user data and creates the user in the database. Returns jwt token that is used for authentication and authorization in the app.
  - note: endpoint is only used in testing to create new users.

- Log in user:
  - path: /api/v1/auth/authenticate
  - method: POST
  - description: Receives email and passowrd and reurns jwt token if the user exists in the database and the credentials match.

- Get user info:
  - path: /api/v1/user/
  - method: GET
  - description: Fetches user info from email provided in the jwt token.
  - note: used only in testing scenarios while developing the app.

- Get doctors for service:
  - path: /api/v1/user/doctors/
  - method: GET
  - description: receives a service name as request parameter and return names of users with the role doctor that provide this service.

- Get user info by id:
  - path: /api/v1/user/userInfo/{id}
  - method: GET
  - description: Fetches user info for the provided user id.
  - note: used only in testing scenarios while developing the app.
 
- Get user id:
  - path: /api/v1/user/userId
  - method: GET
  - description: Fetches user id for the email provided in the jwt token.

- Get services:
  - path: /api/v1/user/services
  - method: GET
  - description: Fetches service names to display in the frontend.

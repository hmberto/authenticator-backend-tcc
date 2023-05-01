<a href="https://www.pucsp.br/home"><img alt="PUS-SP logo" src="https://www5.pucsp.br/paginainicial/images/brasao-pucsp-black.png" height="100" /></a>

# PUC-SP - Passwordless Authentication Backend

This backend serves as the foundation for a Passwordless Authentication system that was developed using Maven, Java 1.8, and the Jetty Maven Plugin version 9.4.51.v20230217.

## Description
This backend provides the core functionality for a Passwordless Authentication system. It handles the generation of one-time passwords (OTPs) and their validation. It also provides APIs for user registration and authentication.

## Installation
To install and run this project, you need to have Java 1.8 and Maven installed on your system. After installing them, follow the steps below:

> For the proper functioning of the API, it is necessary to correctly configure the environment variables API_USER and API_PASS, as they will be responsible for authenticating requests to the API. If this configuration is not set, the application will use default values of "user" and "pass".

> The environment variables CONTEXT_PATH and API_PORT are also used to configure the API. If these variables are not set, the application will use default values of "/api" and "8080", respectively.

- Clone the repository to your local system;
- Navigate to the project directory;
- Run the command **mvn jetty:run** to start the server;
- The server will be started at **http://localhost:8080/api/**;

## Usage
This backend provides the following APIs:

### User Registration Initiation API
**POST** ```/api/register/email```

This service can be used to either register a new user or send an OTP or Access Link to an existing user. It takes the following parameters:

```email``` : The email address of the user to register or receive email if you are already registered<br>
```link``` : Boolean value to request Access Link by email - **optional if it is a new registration**<br>
```otp``` : Boolean value to request OTP by email - **optional if it is a new registration**

If successful, the system will reply with a status code 201 and a JSON response that includes the user ID as ```userId```.

### User Registration Completion API
**PUT** ```/api/register/name```

This service is used to finish the registration of a new user. It takes the following parameters:

```firstName``` : User's first name<br>
```lastName``` : User's last name<br>
```email``` : User's email address<br>
```session``` : Unique and valid session token to perform the update

If successful, the system will reply with a status code 200.

### Validates OTP code
**POST** ```/api/validate/otp```

This service is used to validate the OTP sent by email to the user. It takes the following parameters:

```email``` : User's email address<br>
```otp``` : Unique OTP code for validation

If successful, the system will reply with a status code 200. The JSON response containing the following:

```isLogin``` : Returns true if the user has already completed the registration flow<br>
```session``` : Unique session token for the user<br>
```isSessionTokenActive``` : Confirms if session token is active<br>
```userId``` : User's ID

### Updates the user's session token to active when the user confirms access through the link sent via email,
**POST** ```/api/validate/access-link```

This service is used to validate the Access Link sent by email to the user. It takes the following parameters:

```email``` : User's email address<br>
```approve``` : Boolean value to authorize or deny access<br>
```sessionToken``` : Inactive session token emailed to be validated<br>
```emailToken``` : Unique token sent by email to identify that login attempt

If successful, the system will reply with a status code 200.

### Search the access link information sent by email
**GET** ```/api/check/access-link/{emailToken}```

This service allows to search for information about Access Link sent by email. To use this service, only need to pass the token of the access as a parameter in the URL. The JSON response containing the following:

```createdAt``` : Date the access request was made<br>
```requestBrowser``` : Browser that originated the access request<br>
```requestOS``` : Operating system that originated the access request<br>
```requestIP``` : IP that originated the access request<br>
```sameIP``` : Boolean value that confirm whether the current IP is the same as the one that originated the access request<br>
```isApproved``` : Boolean value that confirms whether the access request has already been approved or not<br>
```userId``` : User's ID

### Check user session status
**POST** ```/api/check/session```

This service is used to verify if the Access Link sent by email to the user was authorized or denied. It also verifies that the user's current session remains active. It takes the following parameters:

```email``` : User's email address<br>
```sessionToken``` : Unique session token

If successful, the system will reply with a status code 200. The JSON response containing the following:

```Status``` : "Valid session", "Unconfirmed session" or "Invalid session token".

### Get user information
**GET** ```/api/users/{email}```

This service allows to search for information about the user through email. To do this, just pass the email as a parameter in the URL. The JSON response containing the following:

```firstName``` : User's first name<br>
```lastName``` : User's last name<br>
```userId``` : User's ID

### Closes active sessions
**PUT** ```/api/logout```

This API is used to end active sessions. It takes the following parameters:

```email``` : User's email address<br>
```session``` : Unique session token<br>
```killAll``` : Boolean value to end all active sessions or just the current session

On successful registration, it returns an OK response with status 200.

## Environment settings
This service utilizes system variables to set up the configuration for its services. These variables can be modified if necessary. The following are the variables used along with their corresponding values:

~~~
EMAIL_BOX="humberto.pucsp@outlook.com"
EMAIL_PASS="email_pass"
EMAIL_SERVER="OUTLOOK/GMAIL"
DB_HOST="localhost"
DB_NAME="tcc-humberto"
DB_USER="user-db"
DB_PASS="user-pass"
OTP_LENGTH="6"
SESSION_LENGTH="100"
EMAIL_TOKEN_LENGTH="50"
SQL_SCRIPT="db/v1_init.sql"
SITE_HOST="https://site_url.com"
TIME_ZONE="America/Sao_Paulo"
API_USER="username"
API_PASS="password"
CONTEXT_PATH="/api"
API_PORT="8080"
~~~

> EMAIL_BOX: This variable specifies the email address that the service will use for sending emails.<br>
> EMAIL_PASS: This variable represents the password associated with the email address provided above.<br>
> EMAIL_SERVER: This variable specifies the email server that the service will use to send emails.<br>
> DB_HOST: This variable indicates the hostname of the database server that the service will connect to.<br>
> DB_NAME: This variable specifies the name of the database that the service will use.<br>
> DB_USER: This variable represents the username that the service will use to authenticate with the database.<br>
> DB_PASS: This variable indicates the password associated with the database user provided above.<br>
> OTP_LENGTH: This variable specifies the length of the One-Time Password (OTP) that the service will generate for user authentication.<br>
> SESSION_LENGTH: This variable specifies the length of the session token that the service will generate to identify a user session.<br>
> EMAIL_TOKEN_LENGTH: 
> SQL_SCRIPT: 
> SITE_HOST: This variable indicates the hostname of the website to which the service will connect and consume this rest service.
> TIME_ZONE: 
> API_USER: 
> API_PASS: 
> CONTEXT_PATH: 
> API_PORT: 

By modifying the values of these variables, the service's configuration can be customized to meet specific requirements.

## Build Instructions

All project is based on maven, so to build the project from the sources you should execute this command:

```shell
$ mvn clean package
```

Or
```shell
$ mvn package jetty:run
```

## References
* [Java](https://www.java.com/) - v1.8
* [Markdown Cheatsheet](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet)

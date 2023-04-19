<a href="https://www.pucsp.br/home"><img alt="PUS-SP logo" src="https://www5.pucsp.br/paginainicial/images/brasao-pucsp-black.png" height="100" /></a>

# PUC-SP - Passwordless Authentication Backend

This is the backend for a Passwordless Authentication system built using Java 1.8.

## Description
This backend provides the core functionality for a Passwordless Authentication system. It handles the generation of one-time passwords (OTPs) and their validation. It also provides APIs for user registration and authentication.

## Installation
To install and run this project, you need to have Java 1.8 and Maven installed on your system. After installing them, follow the steps below:

- Clone the repository to your local system;
- Navigate to the project directory;
- Run the command **mvn package jetty:run** to start the server;
- The server will be started at **http://localhost:8080/api/**;

## Usage
This backend provides the following APIs:

### User Registration Initiation API
**POST** ```/api/register-email```

This API is used to register a new user. It takes the following parameters:

```email``` : The email address of the user

On successful registration, it returns a JSON response containing the following:

```userId``` : The unique ID of the registered user<br>
```session``` : The unique session token to validate the next requests

### User Registration Completion API
**POST** ```/api/register-name```

This API is used to finish the registration of a new user. It takes the following parameters:

```name``` : The full name of the user<br>
```email``` : The email address of the user<br>
```session``` : The unique session token to validate the request

On successful registration, it returns an OK response with status 200.

### Generate and Send new OTP
**POST** ```/api/generate-otp```

This API is used to generate and email a new OTP code to the user. It takes the following parameters:

```email``` : The email address of the user<br>
```code``` : TRUE or FALSE value for email new 6-digit OTP<br>
```link``` : TRUE or FALSE value for email confirmation link

On successful registration, it returns a JSON response containing the following:

```userId``` : The unique ID of the registered user<br>
```session``` : The unique session token to validate the next requests

### Validates 6-digit OTP code and Session Tokens
**POST** ```/api/validate-otp```

This API is used to validate OTP sent by email to the user. It takes the following parameters:

```email``` : The email address of the user<br>
```approve``` : TRUE or FALSE value to approve or disapprove the login request<br>
```tokenOrCode``` : The unique session token or 6-digt code to validate

On successful registration, it returns an OK response with status 200.

### Closes active sessions
**POST** ```/api/logout```

This API is used to end active sessions. It takes the following parameters:

```email``` : The email address of the user<br>
```killAll``` : TRUE or FALSE value to end just the current session or all<br>
```session``` : The unique session token to validate the request

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
~~~

> EMAIL_BOX: This variable specifies the email address that the service will use for sending emails.<br>
> EMAIL_PASS: This variable represents the password associated with the email address provided above.<br>
> EMAIL_SERVER: This variable specifies the email server that the service will use to send emails.<br>
> DB_HOST: This variable indicates the hostname of the database server that the service will connect to.<br>
> DB_NAME: This variable specifies the name of the database that the service will use.<br>
> DB_USER: This variable represents the username that the service will use to authenticate with the database.<br>
> DB_PASS: This variable indicates the password associated with the database user provided above.<br>
> OTP_LENGTH: This variable specifies the length of the One-Time Password (OTP) that the service will generate for user authentication.<br>
> SESSION_LENGTH: This variable specifies the length of the session token that the service will generate to identify a user session.

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

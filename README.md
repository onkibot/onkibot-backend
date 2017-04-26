[OnkiBOT Backend][Website] [![Build Status](https://ci.onkibot.com/api/badges/onkibot/onkibot-backend/status.svg)](https://ci.onkibot.com/onkibot/onkibot-backend) [![codecov](https://codecov.io/gh/onkibot/onkibot-backend/branch/master/graph/badge.svg)](https://codecov.io/gh/onkibot/onkibot-backend)
===================================
Source
------
The latest and greatest source can be found on [GitHub].  

License
-------
Copyright 2017 OnkiBOT Team

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


Requirements
---------
* A Linux/GNU based system<sup>1</sup>.
* [Docker] and [Docker Compose].

<sup>1</sup> [Docker Compose] has issues building our project on Windows based systems.

How to install
---------
1. Clone the repository.
2. Open command line in the local repository.
3. Follow the instructions below in this readme.

How to run local development server
---------
The OnkiBOT Backend uses [Docker], which creates the required database, imports the sql schemas and runs the project.

All you need to do is to edit the `.env` file and set these environment variables:
```
DB_HOST=127.0.0.1
DB_PORT=3306
DB_ROOT_PASSWORD=onkibot
DB_NAME=onkibot
DB_USERNAME=onkibot
DB_PASSWORD=onkibot
```
And run the command below in the root directory of the project.
```
docker-compose -f docker-compose.dev.yml -p onkibot-backend-dev up -d
```
You can also run the project through your IDE, but you would need to set the same environment variables above in your configuration.
`OnkibotBackendApplication` is the entry point for the project.

#### Development tools ####
OnkiBOT Backend uses [Gradle] with [Spring Boot] and we have the following tools for developers:

* [Google Java Style Guide] testing. 
You can verify your code is up to par with the guidelines by executing `./gradlew verifyGoogleJavaFormat`. 
To format your code according to the guidelines you can execute `./gradlew googleJavaFormat`

* We use [JUnit] (through [Gradle]) for our Unit Testing. You can run the tests by executing `./gradlew test`

* In addition to Unit Testing we have added the [JaCoCo Plugin] to generate test and coverage reports. 
You can generate the report by executing `./gradlew jacocoTestReport`

* If you are using the [IntelliJ IDEA] IDE you can generate the project files by running `./gradlew idea`

* If you are using the [Eclipse] IDE you can generate the project files by running `./gradlew eclipse`

How to run the server in production
---------
To run the project in production, you would to the same as for a development environment.

Edit the `.env` file and set these environment variables to **secure** variables:
```
DB_HOST=127.0.0.1
DB_PORT=3306
DB_ROOT_PASSWORD=change_me_to_something_secure
DB_NAME=change_me
DB_USERNAME=change_me
DB_PASSWORD=change_me_to_something_secure
```
And run the command below in the root directory of the project.

**Note** that this step is also different from the development setup (`onkibot-backend-dev != onkibot-backend-prod`).
```
docker-compose -f docker-compose.prod.yml -p onkibot-backend-prod up -d
```

Coding and Pull Request Conventions
-----------------------------------
* Follow the [Google Java Style Guide].
* Use spaces, no tabs.
* No trailing whitespaces.
* 100 column limit for readability.
* Pull requests must compile, have documentation and tests, work, and be formatted properly.
* No merges should be included in pull requests unless the pull request's purpose is a merge.
* Number of commits in a pull request should be kept to *one commit* and all additional commits must be *squashed*.
* You may have more than one commit in a pull request if the commits are separate changes, otherwise squash them.

**Please follow the above conventions if you want your pull request(s) accepted.**

[License]: https://opensource.org/licenses/MIT
[Website]: http://www.onkibot.com
[GitHub]: https://github.com/onkibot/onkibot-backend
[Google Java Style Guide]: https://google.github.io/styleguide/javaguide.html
[Docker]: https://www.docker.com/what-docker
[Docker Compose]: https://docs.docker.com/compose/overview/
[Gradle]: https://gradle.org/
[Spring Boot]: https://projects.spring.io/spring-boot/
[JUnit]: http://junit.org/
[JaCoCo Plugin]: https://docs.gradle.org/current/userguide/jacoco_plugin.html
[IntelliJ IDEA]: https://www.jetbrains.com/idea/
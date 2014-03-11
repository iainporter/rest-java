JAVA REST Application
====================

sample web project that demonstrates the use of:

 * Jersey + JAX-RS
 * Spring Integration
 * Spring Data + Hibernate
 * Groovy Integration tests
 * OAuth
 * Velocity + Java Mail
 * Facebook Login
 * Password Reset
 * Login/Sign Up + Email Verification
 * JSR 303 Validation

to build:

gradle clean build integrationTest

or use the gradle wrapper:

./gradlew clean build integrationTest

go to /build/reports/emma for test coverage reports

to run:

gradle tomcatRun

navigate to http://localhost:8080/java-rest/

see http://porterhead.blogspot.co.uk/2013/01/writing-rest-services-in-java-part-1.html for full details


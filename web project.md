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

 NOTE. For a similar project that uses most of the same components but is built around OAuth2 see
  <a href="http://porterhead.blogspot.co.uk/2014/05/securing-rest-services-with-spring.html">Securing Rest Services with OAuth2 and Spring Security</a>

to build:

gradle clean build integrationTest

or use the gradle wrapper:

./gradlew clean build integrationTest

go to /build/reports/emma for test coverage reports

to run:

gradle tomcatRun

navigate to http://localhost:8080/java-rest/

see blog posts:

<ul>
<li><a href="http://porterhead.blogspot.co.uk/2013/01/writing-rest-services-in-java-part-1.html">Part 1: An introduction to writing REST Services in Java</a></li>
<li><a href="http://porterhead.blogspot.co.uk/2013/01/writing-rest-services-in-java-part-2.html">Part 2: User sign up and login</a></li>
<li><a href="http://porterhead.blogspot.co.uk/2013/01/writing-rest-services-in-java-part-3.html">Part 3: Email Verification</a></li>
<li><a href="http://porterhead.blogspot.co.uk/2013/01/writing-rest-services-in-java-part-4.html">Part 4: Facebook Authentication</a></li>
<li><a href="http://porterhead.blogspot.co.uk/2013/01/writing-rest-services-in-java-part-5.html">Part 5: Lost Password</a></li>
<li><a href="http://porterhead.blogspot.co.uk/2013/01/writing-rest-services-in-java-part-6.html">Part 6: Security &amp; Authorization</a></li>
<li><a href="http://porterhead.blogspot.co.uk/2013/03/writing-rest-services-in-java-part-7.html">Part 7: Moving to Production</a></li>
<li><a href="http://porterhead.blogspot.co.uk/2013/05/writing-rest-services-in-java-part-8.html">Part 8: JSR 303 Validation</a></li>
</ul>


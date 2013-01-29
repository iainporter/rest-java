import groovyx.net.http.ContentType
import static org.hamcrest.Matchers.*
import static org.junit.Assert.assertThat

/**
 * User: porter
 * Date: 09/03/2012
 * Time: 17:41
 */
class UserIntegrationTest extends BaseIntegrationTst {

    def TEST_PASSWORD = "password123"


    public void testSignUpUser() {
		def signupResponse =  httpSignUpUser(createRandomUserName(), TEST_PASSWORD)
		assertEquals(201, signupResponse.status)
        assertTrue(signupResponse.responseData["userId"] != null)
        assertTrue(signupResponse.responseData["token"] != null)
    }

    public void testInvalidRequest() {
		try {
             def signupResponse =  getRestClient().post(path: "user", contentType: ContentType.JSON, body: getNoRootUserRequest(createRandomUserName(), TEST_PASSWORD))
            fail("Expected 500 response")
        } catch (Exception e) {
             assertEquals(500, e.response.status)
        }
    }

    public void testPasswordTooShort() {
		try {
             def signupResponse =  getRestClient().post(path: "user", contentType: ContentType.JSON, body: getCreateUserRequest(createRandomUserName(), "1234"))
            fail("Expected 400 response")
        } catch (Exception e) {
             assertEquals(400, e.response.status)
        }
    }

    public void testUsernameAlreadyExists() {
        try {
            String username = createRandomUserName();
            getRestClient().post(path: "user", contentType: ContentType.JSON, body: getCreateUserRequest(username, TEST_PASSWORD))
            getRestClient().post(path: "user", contentType: ContentType.JSON, body: getCreateUserRequest(username, TEST_PASSWORD))
            fail("Expected 409 response")
        } catch (Exception e) {
            assertEquals(409, e.response.status)
        }
    }

    public void testLogin() {
        def username = createRandomUserName()
        getRestClient().post(path: "user", contentType: ContentType.JSON, body: getCreateUserRequest(username, TEST_PASSWORD))
        def loginResponse = getRestClient().post(path: "user/login", contentType: ContentType.JSON, body: getLoginRequest(username, TEST_PASSWORD))
        assertEquals(200, loginResponse.status)
    }

    public void testInvalidUsernameOnLogin() {
        try {
            def username = createRandomUserName()
            getRestClient().post(path: "user", contentType: ContentType.JSON, body: getCreateUserRequest(username, TEST_PASSWORD))
            getRestClient().post(path: "user/login", contentType: ContentType.JSON, body: getLoginRequest(createRandomUserName(), TEST_PASSWORD))
            fail("Expected 401 response")
        } catch (Exception e) {
            assertEquals(401, e.response.status)
        }
    }

    public void testInvalidPasswordOnLogin() {
        try {
            def username = createRandomUserName()
            getRestClient().post(path: "user", contentType: ContentType.JSON, body: getCreateUserRequest(username, TEST_PASSWORD))
            getRestClient().post(path: "user/login", contentType: ContentType.JSON, body: getLoginRequest(username, "12345678"))
            fail("Expected 401 response")
        } catch (Exception e) {
            assertEquals(401, e.response.status)
        }
    }


    public void testGetUser() {
         //set up user data
        def userResponse = httpSignUpUser(createRandomUserName(), "password")
        def userId = userResponse.responseData["userId"]
        def userToken = userResponse.responseData["token"]
        def getUserResponse = httpGetUser(userToken, userId, userId)
        assertEquals(200, getUserResponse.status)
        def user = getUserResponse.responseData
        assertThat(user.verified, is(false))
    }

    public void testUpdateUser() {
          //set up user data
        def userResponse = httpSignUpUser(createRandomUserName(), "password")
        def userId = userResponse.responseData["userId"]
        def userToken = userResponse.responseData["token"]
        def updateRequest = "{" + getJsonNameValue("firstName", "FOO") + "," + getJsonNameValue("lastName", "BAR") + "," +
                getJsonNameValue("emailAddress", "foobar@example.com") +"}"
        def updateUserResponse = httpUpdateUser(userToken, userId, updateRequest)
        assertEquals(200, updateUserResponse.status)
        def getUserResponse = httpGetUser(userToken, userId, userId)
        assertEquals(200, getUserResponse.status)
        def user = getUserResponse.responseData
        assertThat(user.verified, is(false))
        assertThat(user.firstName, is("FOO"))
        assertThat(user.lastName, is("BAR"))
        assertThat(user.emailAddress, is("foobar@example.com"))
    }

    public void testUserAttemptsToUpdateAnotherUser() {
        //set up user1 data
        def userResponse = httpSignUpUser(createRandomUserName(), "password")
        def userId = userResponse.responseData["userId"]
        def userToken = userResponse.responseData["token"]
        //set up user2 data
        def userResponse2 = httpSignUpUser(createRandomUserName(), "password")
        def userId2 = userResponse2.responseData["userId"]
        def updateRequest = "{" + getJsonNameValue("firstName", "FOO") + "}"
        try {
            def updateUserResponse = httpUpdateUser(userToken, userId2, updateRequest)
            fail("Expected 403 response")
        } catch (Exception e) {
            assertEquals(403, e.response.status)
        }

    }


}

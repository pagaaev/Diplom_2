import base.BaseTest;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.example.User;
import org.example.UserAPI;
import org.example.UserUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateUserTest extends BaseTest {

    private UserAPI userAPI;
    private String accessToken;

    @Before
    public void setUp() {
        userAPI = new UserAPI();
        accessToken = null;
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userAPI.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Check status code of create user")
    public void createNewUserTest() {
        User user = UserUtils.getRandomUser();
        Response response = userAPI.createUser(user);
        response.then().assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true));

        accessToken = response.path("accessToken");
    }

    @Test
    @DisplayName("Check status code of create registered user")
    public void createRegisteredUserTest() {
        User user = UserUtils.getRandomUser();
        Response createResponse = userAPI.createUser(user);
        accessToken = createResponse.path("accessToken");

        Response createDoubleResponse = userAPI.createUser(user);
        createDoubleResponse.then().assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Check status code of create user without email")
    public void createUserWithoutEmailTest() {
        User user = UserUtils.getRandomUser();
        user.setEmail(null);
        Response createResponse = userAPI.createUser(user);
        createResponse.then().assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Check status code of create user without password")
    public void createUserWithoutPasswordTest() {
        User user = UserUtils.getRandomUser();
        user.setPassword(null);
        Response createResponse = userAPI.createUser(user);
        createResponse.then().assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Check status code of create user without name")
    public void createUserWithoutNameTest() {
        User user = UserUtils.getRandomUser();
        user.setName(null);
        Response createResponse = userAPI.createUser(user);
        createResponse.then().assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}

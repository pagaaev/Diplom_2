import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.User;
import org.example.UserAPI;
import org.example.UserUtils;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateUserTest {
    private UserAPI userAPI;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        userAPI = new UserAPI();
    }

    @Test
    @DisplayName("Check status code of create user")
    public void createNewUserTest() {
        User user = UserUtils.getRandomUser();
        Response response = userAPI.createUser(user);
        response.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);
        String accessToken = response.path("accessToken");
        userAPI.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Check status code of create registered user")
    public void createRegisteredUserTest() {
        User user = UserUtils.getRandomUser();
        Response createResponse = userAPI.createUser(user);
        Response createDoubleResponse = userAPI.createUser(user);
        createDoubleResponse.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(SC_FORBIDDEN);
        String accessToken = createResponse.path("accessToken");
        userAPI.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Check status code of create user without email")
    public void createUserWithoutEmailTest() {
        User user = UserUtils.getRandomUser();
        user.setEmail(null);
        Response createResponse = userAPI.createUser(user);
        createResponse.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Check status code of create user without password")
    public void createUserWithoutPasswordTest() {
        User user = UserUtils.getRandomUser();
        user.setPassword(null);
        Response createResponse = userAPI.createUser(user);
        createResponse.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Check status code of create user without name")
    public void createUserWithoutNameTest() {
        User user = UserUtils.getRandomUser();
        user.setName(null);
        Response createResponse = userAPI.createUser(user);
        createResponse.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(SC_FORBIDDEN);
    }
}

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

public class LoginUserTest {
    private UserAPI userAPI;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        userAPI = new UserAPI();
    }

    @Test
    @DisplayName("Check status code of login user")
    public void loginUserTest() {
        UserUtils userUtils;
        User user = UserUtils.getRandomUser();
        userAPI.createUser(user);
        Response loginResponse = userAPI.loginUser(user);
        loginResponse.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);
        String accessToken = loginResponse.path("accessToken");
        userAPI.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Check status code of login user with wrong email")
    public void loginUserWithWrongEmailTest() {
        User user = UserUtils.getRandomUser();
        Response loginResponse = userAPI.loginUser(user);
        loginResponse.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Check status code of login user with wrong password")
    public void loginUserWithWrongPasswordTest() {
        User user = UserUtils.getRandomUser();
        Response createResponse = userAPI.createUser(user);
        user.setPassword(user.getPassword() + "1230");
        Response loginResponse = userAPI.loginUser(user);
        loginResponse.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(SC_UNAUTHORIZED);
        String accessToken = createResponse.path("accessToken");
        userAPI.deleteUser(accessToken);
    }
}

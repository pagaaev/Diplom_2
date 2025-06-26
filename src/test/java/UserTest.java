import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.User;
import org.example.UserAPI;
import org.example.UserUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

public class UserTest {
    private UserAPI userAPI;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        userAPI = new UserAPI();
    }

    @Test
    @DisplayName("Check status code of change authorized user")
    public void changeAuthorizedUserTest() {
        User user = UserUtils.getRandomUser();
        Response createResponse = userAPI.createUser(user);
        User changedUser = UserUtils.getRandomUser();
        String accessToken = createResponse.path("accessToken");
        Response changeResponse = userAPI.changeUser(changedUser, accessToken);

        changeResponse.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);
        String email = changeResponse.path("user.email");
        String name = changeResponse.path("user.name");

        Assert.assertThat(email, equalToIgnoringCase(changedUser.getEmail()));
        Assert.assertThat(name, equalToIgnoringCase(changedUser.getName()));

        Response loginResponse = userAPI.loginUser(changedUser);
        loginResponse.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);

        accessToken = loginResponse.path("accessToken");
        userAPI.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Check status code of change authorized user with existing email")
    public void changeAuthorizedUserWithExistingEmailTest() {
        User firstUser = UserUtils.getRandomUser();
        Response createFirstUserResponse = userAPI.createUser(firstUser);
        String firstUserAccessToken = createFirstUserResponse.path("accessToken");
        User secondUser = UserUtils.getRandomUser();
        Response createSecondUserResponse = userAPI.createUser(secondUser);
        String secondUserAccessToken = createSecondUserResponse.path("accessToken");

        firstUser.setEmail(secondUser.getEmail());
        Response changeResponse = userAPI.changeUser(firstUser, firstUserAccessToken);
        changeResponse.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(SC_FORBIDDEN);

        userAPI.deleteUser(firstUserAccessToken);
        userAPI.deleteUser(secondUserAccessToken);
    }

    @Test
    @DisplayName("Check status code of change unauthorized user")
    public void changeUnauthorizedUserTest() {
        User user = UserUtils.getRandomUser();
        Response createResponse = userAPI.createUser(user);
        String accessToken = createResponse.path("accessToken");
        User changedUser = UserUtils.getRandomUser();
        Response changeResponse = userAPI.changeUser(changedUser, "");
        changeResponse.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(SC_UNAUTHORIZED);
        userAPI.deleteUser(accessToken);
    }
}

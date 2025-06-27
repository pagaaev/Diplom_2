import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.example.User;
import org.example.UserAPI;
import org.example.UserUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

public class UserTest extends BaseTest {

    private UserAPI userAPI;
    private String accessToken;

    @Before
    public void setUp() {
        userAPI = new UserAPI();
        accessToken = null;
    }

    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            deleteUser(accessToken);
            accessToken = null;
        }
    }

    @Step("Создать пользователя: {user.email}")
    private Response createUser(User user) {
        return userAPI.createUser(user);
    }

    @Step("Изменить пользователя: {changedUser.email}")
    private Response changeUser(User changedUser, String accessToken) {
        return userAPI.changeUser(changedUser, accessToken);
    }

    @Step("Удалить пользователя с токеном: {accessToken}")
    private void deleteUser(String accessToken) {
        userAPI.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Check status code of change authorized user")
    public void changeAuthorizedUserTest() {
        User user = UserUtils.getRandomUser();
        Response createResponse = createUser(user);
        accessToken = createResponse.path("accessToken");

        User changedUser = UserUtils.getRandomUser();
        Response changeResponse = changeUser(changedUser, accessToken);

        changeResponse.then().assertThat()
                .body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);

        String email = changeResponse.path("user.email");
        String name = changeResponse.path("user.name");

        Assert.assertThat(email, equalToIgnoringCase(changedUser.getEmail()));
        Assert.assertThat(name, equalToIgnoringCase(changedUser.getName()));

        Response loginResponse = userAPI.loginUser(changedUser);
        loginResponse.then().assertThat()
                .body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);

        accessToken = loginResponse.path("accessToken");
    }

    @Test
    @DisplayName("Check status code of change authorized user with existing email")
    public void changeAuthorizedUserWithExistingEmailTest() {
        User firstUser = UserUtils.getRandomUser();
        Response createFirstUserResponse = createUser(firstUser);
        String firstUserAccessToken = createFirstUserResponse.path("accessToken");

        User secondUser = UserUtils.getRandomUser();
        Response createSecondUserResponse = createUser(secondUser);
        String secondUserAccessToken = createSecondUserResponse.path("accessToken");

        firstUser.setEmail(secondUser.getEmail());
        Response changeResponse = changeUser(firstUser, firstUserAccessToken);

        changeResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("User with such email already exists"))
                .and()
                .statusCode(SC_FORBIDDEN);

        deleteUser(firstUserAccessToken);
        deleteUser(secondUserAccessToken);
    }

    @Test
    @DisplayName("Check status code of change unauthorized user")
    public void changeUnauthorizedUserTest() {
        User user = UserUtils.getRandomUser();
        Response createResponse = createUser(user);
        accessToken = createResponse.path("accessToken");

        User changedUser = UserUtils.getRandomUser();
        Response changeResponse = changeUser(changedUser, "");

        changeResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }
}

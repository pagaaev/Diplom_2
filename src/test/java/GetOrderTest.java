import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.OrderAPI;
import org.example.UserAPI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class GetOrderTest {
    private OrderAPI orderAPI;
    private UserAPI userAPI;
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        orderAPI = new OrderAPI();
        userAPI = new UserAPI();
        accessToken = userAPI.createUser();  // Создаем пользователя перед каждым тестом
    }

    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            userAPI.deleteUser(accessToken);  // Удаляем пользователя после каждого теста
        }
    }

    @Test
    @DisplayName("Check status code of get orders")
    public void getOrdersTest() {
        Response response = orderAPI.getOrders(accessToken);
        response.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Check status code of get orders without authorization")
    public void getOrdersWithoutAuthorizationTest() {
        Response response = orderAPI.getOrders("");
        response.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }
}

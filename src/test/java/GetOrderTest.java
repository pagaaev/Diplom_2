import base.BaseTest;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.example.OrderAPI;
import org.example.UserAPI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class GetOrderTest extends BaseTest {

    private OrderAPI orderAPI;
    private UserAPI userAPI;
    private String accessToken;

    @Before
    public void setUp() {
        orderAPI = new OrderAPI();
        userAPI = new UserAPI();
        accessToken = userAPI.createUser();
    }

    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            userAPI.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Check status code of get orders")
    public void getOrdersTest() {
        Response response = orderAPI.getOrders(accessToken);
        response.then().assertThat()
                .body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Check status code of get orders without authorization")
    public void getOrdersWithoutAuthorizationTest() {
        Response response = orderAPI.getOrders("");
        response.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised")) // Проверяем текст ошибки
                .and()
                .statusCode(SC_UNAUTHORIZED);
    }
}

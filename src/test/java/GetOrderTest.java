import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.OrderAPI;
import org.example.UserAPI;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class GetOrderTest {
    private OrderAPI orderAPI;
    private UserAPI userAPI;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        orderAPI = new OrderAPI();
        userAPI = new UserAPI();
    }

    @Test
    @DisplayName("Check status code of get orders")
    public void getOrdersTest() {
        String accessToken = userAPI.createUser();
        Response response = orderAPI.getOrders(accessToken);
        response.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);
        userAPI.deleteUser(accessToken);
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

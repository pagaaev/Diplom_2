import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Ingredient;
import org.example.OrderAPI;
import org.example.UserAPI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateOrderTest {

    private OrderAPI orderAPI;
    private UserAPI userAPI;
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
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
    @DisplayName("Check status code of create order")
    public void createOrderTest() {
        List<Ingredient> ingredients = orderAPI.getIngredients();
        List<String> ingredientIds = new ArrayList<>();
        ingredientIds.add(ingredients.get(0).get_id());
        ingredientIds.add(ingredients.get(1).get_id());
        ingredientIds.add(ingredients.get(2).get_id());

        Response response = orderAPI.createOrder(ingredientIds, accessToken);
        response.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Check status code of create order with wrong ingredients")
    public void createOrderWithWrongIngredientsTest() {
        List<String> ingredientIds = new ArrayList<>();
        ingredientIds.add("wrongid");

        Response response = orderAPI.createOrder(ingredientIds, accessToken);
        response.then().assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Check status code of create order without ingredients")
    public void createOrderWithoutIngredientsTest() {
        List<String> ingredientIds = new ArrayList<>();

        Response response = orderAPI.createOrder(ingredientIds, accessToken);
        response.then().assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Check status code of create order by unauthorized user")
    public void createOrderByUnauthorizedUserTest() {
        List<Ingredient> ingredients = orderAPI.getIngredients();
        List<String> ingredientIds = new ArrayList<>();
        ingredientIds.add(ingredients.get(0).get_id());
        ingredientIds.add(ingredients.get(1).get_id());
        ingredientIds.add(ingredients.get(2).get_id());

        Response response = orderAPI.createOrder(ingredientIds, "");
        response.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }
}

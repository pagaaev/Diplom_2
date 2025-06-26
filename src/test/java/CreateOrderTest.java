import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Ingredient;
import org.example.OrderAPI;
import org.example.UserAPI;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;

public class CreateOrderTest {

    private OrderAPI orderAPI;
    private UserAPI userAPI;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        orderAPI = new OrderAPI();
        userAPI = new UserAPI();
    }

    @Test
    @DisplayName("Check status code of create order")
    public void createOrderTest() {
        String accessToken = userAPI.createUser();
        List<Ingredient> ingredients = orderAPI.getIngredients();
        List<String> ingredientIds = new ArrayList<>();
        ingredientIds.add(ingredients.get(0).get_id());
        ingredientIds.add(ingredients.get(1).get_id());
        ingredientIds.add(ingredients.get(2).get_id());
        Response response = orderAPI.createOrder(ingredientIds, accessToken);
        response.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);
        userAPI.deleteUser(accessToken);
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
        response.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Check status code of create order without ingredients")
    public void createOrderWithoutIngredientsTest() {
        String accessToken = userAPI.createUser();
        List<String> ingredientIds = new ArrayList<>();
        Response response = orderAPI.createOrder(ingredientIds, accessToken);
        response.then().assertThat().body("success", equalTo(false))
                .and()
                .statusCode(SC_BAD_REQUEST);
        userAPI.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Check status code of create order with wrong ingredients")
    public void createOrderWithWrongIngredientsTest() {
        String accessToken = userAPI.createUser();
        List<String> ingredientIds = new ArrayList<>();
        ingredientIds.add("wrongid");
        Response response = orderAPI.createOrder(ingredientIds, accessToken);
        response.then().assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
        userAPI.deleteUser(accessToken);
    }
}

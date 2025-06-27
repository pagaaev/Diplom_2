package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OrderAPI {
    private static final String ORDER_URL = "/api/orders/";
    private static final String INGREDIENT_URL = "/api/ingredients/";

    @Step("Получение списка ингредиентов")
    public List<Ingredient> getIngredients() {
        Response response = given().get(INGREDIENT_URL);
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonData = jsonParser.parse(response.asPrettyString())
                .getAsJsonObject()
                .get("data");
        Gson gson = new Gson();
        return gson.fromJson(jsonData, new TypeToken<List<Ingredient>>() {
        }.getType());
    }

    @Step("Создание заказа с ингредиентами: {ingredientIds}")
    public Response createOrder(List<String> ingredientIds, String accessToken) {
        Map<String, List<String>> requestParams = new HashMap<>();
        requestParams.put("ingredients", ingredientIds);

        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .and()
                .body(requestParams)
                .when()
                .post(ORDER_URL);
    }

    @Step("Получение заказов текущего пользователя")
    public Response getOrders(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .get(ORDER_URL);
    }
}

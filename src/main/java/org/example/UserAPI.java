package org.example;

import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class UserAPI {
    private static final String REGISTER_URL = "/api/auth/register/";
    private static final String USER_URL = "/api/auth/user/";
    private static final String LOGIN_URL = "/api/auth/login/";
    private static final String LOGOUT_URL = "/api/auth/logout/";

    public Response createUser(User user) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(user)
                        .when()
                        .post(REGISTER_URL);
        return response;
    }

    public String createUser() {
        User user = UserUtils.getRandomUser();
        Response response = createUser(user);
        return response.path("accessToken");
    }

    public Response deleteUser(String token) {
        Response response =
                given()
                        .header("Authorization", token)
                        .delete(USER_URL);
        return response;
    }

    public Response loginUser(User user) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(user)
                        .when()
                        .post(LOGIN_URL);
        return response;
    }

    public Response logoutUser(String token) {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("token", token);
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(requestParams)
                        .when()
                        .post(LOGOUT_URL);
        return response;
    }

    public Response changeUser(User user, String token) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .header("Authorization", token)
                        .and()
                        .body(user)
                        .when()
                        .patch(USER_URL);
        return response;
    }
}

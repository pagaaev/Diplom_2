package base;

import io.restassured.RestAssured;
import org.junit.BeforeClass;

public class BaseTest {

    @BeforeClass
    public static void setupClass() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }
}

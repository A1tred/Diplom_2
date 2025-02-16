package tests;

import clients.IngredientClient;
import clients.UserClient;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.List;

public class BaseTest {

    protected static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    protected UserClient userClient;
    protected IngredientClient ingredientClient;
    protected String accessToken;

    @BeforeClass
    public static void setUpClass() {
        RestAssured.baseURI = BASE_URL;
    }

    @Before
    public void setUp() {
        userClient = new UserClient();
        ingredientClient = new IngredientClient();
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken).then().statusCode(202);
            accessToken = null;
        }
    }

    @Step("Получение списка ID ингредиентов")
    public List<String> getIngredientIds() {
        Response response = ingredientClient.getIngredients();
        response.then().statusCode(200);
        return response.then().extract().path("data._id");
    }
}

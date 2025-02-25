package clients;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class IngredientClient {

    @Step("Получение списка ингредиентов")
    public Response getIngredients() {
        return given()
                .filter(new AllureRestAssured())
                .when()
                .get("/api/ingredients");
    }
}

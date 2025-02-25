package clients;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import models.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {

    @Step("Создание заказа")
    public Response createOrder(String token, Order order) {
        if (token != null) {
            return given()
                    .filter(new AllureRestAssured())
                    .header("Authorization", token)
                    .header("Content-type", "application/json")
                    .body(order)
                    .when()
                    .post("/api/orders");
        } else {
            return given()
                    .filter(new AllureRestAssured())
                    .header("Content-type", "application/json")
                    .body(order)
                    .when()
                    .post("/api/orders");
        }
    }

    @Step("Получение заказов пользователя")
    public Response getUserOrders(String token) {
        if (token != null) {
            return given()
                    .filter(new AllureRestAssured())
                    .header("Authorization", token)
                    .when()
                    .get("/api/orders");
        } else {
            return given()
                    .filter(new AllureRestAssured())
                    .when()
                    .get("/api/orders");
        }
    }
}

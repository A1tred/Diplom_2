package clients;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import models.User;
import models.UserCredentials;

import static io.restassured.RestAssured.given;

public class UserClient {

    @Step("Регистрация пользователя")
    public Response registerUser(User user) {
        return given()
                .filter(new AllureRestAssured())
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register");
    }

    @Step("Логин пользователя")
    public Response loginUser(UserCredentials credentials) {
        return given()
                .filter(new AllureRestAssured())
                .header("Content-type", "application/json")
                .body(credentials)
                .when()
                .post("/api/auth/login");
    }

    @Step("Обновление данных пользователя")
    public Response updateUserData(String token, User userData) {
        if (token != null) {
            return given()
                    .filter(new AllureRestAssured())
                    .header("Authorization", token)
                    .header("Content-type", "application/json")
                    .body(userData)
                    .when()
                    .patch("/api/auth/user");
        } else {
            return given()
                    .filter(new AllureRestAssured())
                    .header("Content-type", "application/json")
                    .body(userData)
                    .when()
                    .patch("/api/auth/user");
        }
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String token) {
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", token)
                .when()
                .delete("/api/auth/user");
    }
}

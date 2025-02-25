package tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.User;
import models.UserCredentials;
import models.UserGenerator;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class LoginUserTest extends BaseTest {

    private User user;
    private UserCredentials userCredentials;

    @Before
    @Override
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    public void loginWithValidCredentialsTest() {
        // Регистрируем пользователя
        user = UserGenerator.getRandomUser();
        userClient.registerUser(user).then().statusCode(200);

        // Получаем UserCredentials для логина
        userCredentials = UserGenerator.getUserCredentials(user);

        // Логинимся под существующим пользователем
        Response response = userClient.loginUser(userCredentials);
        response.then().statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()));

        // Сохраняем accessToken для очистки данных
        accessToken = response.then().extract().path("accessToken");
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    public void loginWithInvalidCredentialsTest() {
        // Регистрируем пользователя
        user = UserGenerator.getRandomUser();
        userClient.registerUser(user).then().statusCode(200);

        // Получаем UserCredentials для логина
        userCredentials = UserGenerator.getUserCredentials(user);

        // Создаем объект UserCredentials с неверным паролем
        UserCredentials invalidCredentials = new UserCredentials(user.getEmail(), "wrongPassword");

        // Пытаемся залогиниться с неверным паролем
        userClient.loginUser(invalidCredentials)
                .then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));

        // Логинимся с корректными данными, чтобы получить accessToken для удаления пользователя
        Response loginResponse = userClient.loginUser(userCredentials);
        loginResponse.then().statusCode(200);
        accessToken = loginResponse.then().extract().path("accessToken");
    }

    @Test
    @DisplayName("Логин с неверным логином")
    public void loginWithInvalidEmailTest() {
        // Создаем объект UserCredentials с неверным email
        UserCredentials invalidCredentials = new UserCredentials("nonexistent@example.com", "password");

        // Пытаемся залогиниться с неверным email
        userClient.loginUser(invalidCredentials)
                .then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
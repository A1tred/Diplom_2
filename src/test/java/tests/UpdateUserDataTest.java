package tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.User;
import models.UserCredentials;
import models.UserGenerator;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class UpdateUserDataTest extends BaseTest {

    private User originalUser;
    private UserCredentials originalUserCredentials;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        originalUser = UserGenerator.getRandomUser();

        // Регистрируем пользователя
        Response registerResponse = userClient.registerUser(originalUser);
        registerResponse.then().statusCode(200);

        // Получаем UserCredentials для логина
        originalUserCredentials = UserGenerator.getUserCredentials(originalUser);

        // Получаем accessToken
        Response loginResponse = userClient.loginUser(originalUserCredentials);
        loginResponse.then().statusCode(200);
        accessToken = loginResponse.then().extract().path("accessToken");
    }

    @Test
    @DisplayName("Изменение данных пользователя с авторизацией")
    public void updateUserDataWithAuthorizationTest() {
        User updatedUser = UserGenerator.getRandomUser();

        // Обновляем все данные пользователя с авторизацией
        Response response = userClient.updateUserData(accessToken, updatedUser);
        response.then().statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(updatedUser.getEmail().toLowerCase()))
                .body("user.name", equalTo(updatedUser.getName()));

        // Проверяем, что можем залогиниться с новыми данными
        UserCredentials newCredentials = UserGenerator.getUserCredentials(updatedUser);
        Response loginResponse = userClient.loginUser(newCredentials);
        loginResponse.then().statusCode(200)
                .body("success", equalTo(true));

        // Обновляем accessToken для корректной очистки после теста
        accessToken = loginResponse.then().extract().path("accessToken");
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    public void updateUserDataWithoutAuthorizationTest() {
        User updatedUser = UserGenerator.getRandomUser();

        // Пытаемся обновить данные без авторизации
        Response response = userClient.updateUserData(null, updatedUser);
        response.then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Изменение только email пользователя с авторизацией")
    public void updateUserEmailWithAuthorizationTest() {
        // Создаём пользователя с новым email, остальные данные остаются прежними
        User updatedUser = new User.Builder()
                .withEmail(UserGenerator.getRandomEmail())
                .withName(originalUser.getName())
                .withPassword(originalUser.getPassword())
                .build();

        // Обновляем email пользователя
        Response response = userClient.updateUserData(accessToken, updatedUser);
        response.then().statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(updatedUser.getEmail().toLowerCase()))
                .body("user.name", equalTo(originalUser.getName()));

        // Проверяем возможность логина с новым email и старым паролем
        UserCredentials newCredentials = UserGenerator.getUserCredentials(updatedUser);
        Response loginResponse = userClient.loginUser(newCredentials);
        loginResponse.then().statusCode(200)
                .body("success", equalTo(true));

        // Обновляем accessToken для корректной очистки после теста
        accessToken = loginResponse.then().extract().path("accessToken");
    }

    @Test
    @DisplayName("Изменение только имени пользователя с авторизацией")
    public void updateUserNameWithAuthorizationTest() {
        // Создаём пользователя с новым именем, остальные данные остаются прежними
        User updatedUser = new User.Builder()
                .withEmail(originalUser.getEmail())
                .withName(UserGenerator.getRandomName())
                .withPassword(originalUser.getPassword())
                .build();

        // Обновляем имя пользователя
        Response response = userClient.updateUserData(accessToken, updatedUser);
        response.then().statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(originalUser.getEmail().toLowerCase()))
                .body("user.name", equalTo(updatedUser.getName()));

        // Проверяем возможность логина с прежними данными
        Response loginResponse = userClient.loginUser(originalUserCredentials);
        loginResponse.then().statusCode(200)
                .body("success", equalTo(true));

        // accessToken остаётся прежним
    }

    @Test
    @DisplayName("Изменение только пароля пользователя с авторизацией")
    public void updateUserPasswordWithAuthorizationTest() {
        // Создаём пользователя с новым паролем, остальные данные остаются прежними
        User updatedUser = new User.Builder()
                .withEmail(originalUser.getEmail())
                .withName(originalUser.getName())
                .withPassword(UserGenerator.getRandomPassword())
                .build();

        // Обновляем пароль пользователя
        Response response = userClient.updateUserData(accessToken, updatedUser);
        response.then().statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(originalUser.getEmail().toLowerCase()))
                .body("user.name", equalTo(originalUser.getName()));

        // Проверяем невозможность логина с прежними данными
        Response oldLoginResponse = userClient.loginUser(originalUserCredentials);
        oldLoginResponse.then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));

        // Проверяем возможность логина с новым паролем
        UserCredentials newCredentials = new UserCredentials(originalUser.getEmail(), updatedUser.getPassword());
        Response loginResponse = userClient.loginUser(newCredentials);
        loginResponse.then().statusCode(200)
                .body("success", equalTo(true));

        // Обновляем accessToken для корректной очистки после теста
        accessToken = loginResponse.then().extract().path("accessToken");
    }
}

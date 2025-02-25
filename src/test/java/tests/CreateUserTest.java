package tests;

import models.User;
import models.UserGenerator;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class CreateUserTest extends BaseTest {

    private User uniqueUser;
    private User existingUser;

    @Test
    public void createUniqueUserTest() {
        uniqueUser = UserGenerator.getRandomUser();

        accessToken = userClient.registerUser(uniqueUser)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(uniqueUser.getEmail().toLowerCase()))
                .body("user.name", equalTo(uniqueUser.getName()))
                .extract().path("accessToken");
    }

    @Test
    public void createExistingUserTest() {
        existingUser = UserGenerator.getRandomUser();

        // Регистрируем пользователя в первый раз
        accessToken = userClient.registerUser(existingUser)
                .then().statusCode(200)
                .extract().path("accessToken");

        // Пытаемся зарегистрировать того же пользователя второй раз
        userClient.registerUser(existingUser)
                .then().statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    public void createUserWithoutRequiredFieldTest() {
        User userWithoutPassword = new User.Builder()
                .withEmail("test@example.com")
                .withName("TestUser")
                .build();

        userClient.registerUser(userWithoutPassword)
                .then().statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}

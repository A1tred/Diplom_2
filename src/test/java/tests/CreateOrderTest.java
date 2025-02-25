package tests;

import clients.IngredientClient;
import clients.OrderClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.Order;
import models.User;
import models.UserCredentials;
import models.UserGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.*;

public class CreateOrderTest extends BaseTest {

    private OrderClient orderClient;
    private User user;
    private UserCredentials userCredentials;
    private List<String> ingredients;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        orderClient = new OrderClient();
        ingredientClient = new IngredientClient();

        // Получаем список ингредиентов
        ingredients = getIngredientIds();
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void createOrderWithAuthorizationTest() {
        // Регистрируем и авторизуем пользователя
        registerAndLoginUser();

        // Создаем заказ
        Order order = new Order(ingredients);
        Response response = orderClient.createOrder(accessToken, order);
        response.then().statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue())
                .body("order", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWithoutAuthorizationTest() {
        // Создаем заказ без авторизации
        Order order = new Order(ingredients);
        Response response = orderClient.createOrder(null, order);
        response.then().statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue())
                .body("order", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithInvalidIngredientHashTest() {
        // Регистрируем и авторизуем пользователя
        registerAndLoginUser();

        // Используем неверный хеш ингредиента
        List<String> invalidIngredients = List.of("invalidHash");
        Order order = new Order(invalidIngredients);

        Response response = orderClient.createOrder(accessToken, order);
        response.then().statusCode(500);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        // Регистрируем и авторизуем пользователя
        registerAndLoginUser();

        // Создаем заказ без ингредиентов
        Order order = new Order(null);
        Response response = orderClient.createOrder(accessToken, order);
        response.then().statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    // Метод для регистрации и авторизации пользователя
    private void registerAndLoginUser() {
        user = UserGenerator.getRandomUser();
        userClient.registerUser(user).then().statusCode(200);
        userCredentials = UserGenerator.getUserCredentials(user);
        accessToken = userClient.loginUser(userCredentials).then().extract().path("accessToken");
    }
}
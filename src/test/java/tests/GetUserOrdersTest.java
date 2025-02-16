package tests;

import clients.IngredientClient;
import clients.OrderClient;
import io.qameta.allure.Step;
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

public class GetUserOrdersTest extends BaseTest {

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

        // Регистрируем пользователя
        user = UserGenerator.getRandomUser();
        userClient.registerUser(user).then().statusCode(200);

        // Получаем UserCredentials для логина
        userCredentials = UserGenerator.getUserCredentials(user);

        // Получаем accessToken
        accessToken = userClient.loginUser(userCredentials).then().extract().path("accessToken");

        // Создаем заказы для пользователя
        createOrdersForUser(2);
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    public void getOrdersWithAuthorizationTest() {
        Response response = orderClient.getUserOrders(accessToken);
        response.then().statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue())
                .body("orders.size()", greaterThan(0));
    }

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя")
    public void getOrdersWithoutAuthorizationTest() {
        Response response = orderClient.getUserOrders(null);
        response.then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Step("Создание заказов для пользователя")
    public void createOrdersForUser(int numberOfOrders) {
        Order order = new Order(ingredients);
        for (int i = 0; i < numberOfOrders; i++) {
            orderClient.createOrder(accessToken, order)
                    .then()
                    .statusCode(200)
                    .body("success", equalTo(true));
        }
    }
}

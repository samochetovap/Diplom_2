import api.OrderApi;
import api.UserApi;
import builder.UserBuilder;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.User;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrderByUserTest {

    private User user;

    @Before
    public void setUp() {
        user = UserBuilder.generateUser();
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя с авторизацией")
    public void getOrderByAuthUser() {
        Response responseRegister = UserApi.register(user);
        String accessToken = responseRegister.then().extract().body().path("accessToken").toString();
        Response createOrderResponse = OrderApi.getOrderByToken(accessToken);
        createOrderResponse.then().assertThat().statusCode(200).body("success", is(true))
                .and().body("orders", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов без авторизации")
    public void getOrderByNotAuthUser() {
        Response createOrderResponse = OrderApi.getOrderWithoutToken();
        createOrderResponse.then().assertThat().statusCode(401).body("success", is(false))
                .and().body("message", is("You should be authorised"));
    }

    @After
    public void deleteUserIfExists() {
        UserApi.deleteUserIfExist(user);
    }
}

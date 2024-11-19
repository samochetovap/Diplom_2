import api.OrderApi;
import api.UserApi;
import builder.IngredientBuilder;
import builder.UserBuilder;
import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.Ingredient;
import pojo.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrder {

    private User user;
    List<Ingredient> ingredients;

    @Before
    public void setUp() {
        user = UserBuilder.generateUser();
        ingredients = OrderApi.getIngredients().then().extract().body().jsonPath().getList("data", Ingredient.class);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и с ингредиентами")
    public void createOrderWithAuthAndIngredients() {
        Response responseRegister = UserApi.register(user);
        String accessToken = responseRegister.then().extract().body().path("accessToken").toString();
        Response createOrderResponse = OrderApi.createOrderByTokenAndIngredients(accessToken,ingredients);
        createOrderResponse.then().assertThat().statusCode(200).body("success", is(true))
                .and().body("name", notNullValue())
                .and().body("order.number", notNullValue())
                .and().body("order.status", is("done"));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и без ингридиентов")
    public void createOrderWithAuthAndWithoutIngredients() {
        Response responseRegister = UserApi.register(user);
        String accessToken = responseRegister.then().extract().body().path("accessToken").toString();
        Response createOrderResponse = OrderApi.createOrderWithToken(accessToken);
        createOrderResponse.then().assertThat().statusCode(400).body("success", is(false))
                .and().body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа без авторизации и без ингредиентов")
    public void createOrderWithoutAuthAndWithoutIngredients() {
        Response createOrderResponse = OrderApi.createOrder();
        createOrderResponse.then().assertThat().statusCode(400).body("success", is(false))
                .and().body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа без авторизации и с ингредиентами")
    public void createOrderWithoutAuthAndWithIngredients() {
        Response createOrderResponse = OrderApi.createOrderWithIngredients(ingredients);
        createOrderResponse.then().assertThat().statusCode(200).body("success", is(true))
                .and().body("name", notNullValue())
                .and().body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithNotAvailableId() {
        List<Ingredient> notAvailableIngredients = new ArrayList<>();
        Ingredient ingredient1 = IngredientBuilder.generateIngredient();
        Ingredient ingredient2 = IngredientBuilder.generateIngredient();
        notAvailableIngredients.add(ingredient1);
        notAvailableIngredients.add(ingredient2);
        Response createOrderResponse = OrderApi.createOrderWithIngredients(notAvailableIngredients);
        createOrderResponse.then().assertThat().statusCode(500);
    }

    @After
    public void deleteUserIfExists() {
        UserApi.deleteUserIfExist(user);
    }

}

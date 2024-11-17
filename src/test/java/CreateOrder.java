import com.google.gson.Gson;
import io.qameta.allure.Step;
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
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";

        user = new User("some12332112332111@gmail.com", "somepassword12345", "someUserName");

        ingredients = given()
                .contentType(ContentType.JSON)
                .get("/api/ingredients").then().extract().body().jsonPath().getList("data", Ingredient.class);
    }

    @Test
    @Step("Создание заказа с авторизацией и с ингридиентами")
    public void createOrderWithAuthAndIngredients() {
        Response responseRegister = registerUser(user);
        String accessToken = responseRegister.then().extract().body().path("accessToken").toString();

        Gson gson = new Gson();
        List<String> ingIds = new ArrayList<>();
        ingIds.add(ingredients.get(0).get_id());
        ingIds.add(ingredients.get(1).get_id());

        String body = "{\"ingredients\":" + gson.toJson(ingIds) + "}";

        Response createOrderResponse = given().header("authorization", accessToken).contentType(ContentType.JSON).and()
                .body(body).and().post("/api/orders");
        System.out.println(createOrderResponse.prettyPrint());
        createOrderResponse.then().assertThat().statusCode(200).body("success", is(true))
                .and().body("name", notNullValue())
                .and().body("order.number", notNullValue())
                .and().body("order.status", is("done"));
    }

    @Test
    @Step("Создание заказа с авторизацией и без ингридиентов")
    public void createOrderWithAuthAndWithoutIngredients() {
        Response responseRegister = registerUser(user);
        String accessToken = responseRegister.then().extract().body().path("accessToken").toString();
        Response createOrderResponse = given().header("authorization", accessToken).contentType(ContentType.JSON).and()
                .post("/api/orders");
        createOrderResponse.then().assertThat().statusCode(400).body("success", is(false))
                .and().body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @Step("Создание заказа без авторизации и без ингридиентов")
    public void createOrderWithoutAuthAndWithoutIngredients() {
        Response createOrderResponse = given().contentType(ContentType.JSON).and()
                .post("/api/orders");
        System.out.println(createOrderResponse.prettyPrint());
        createOrderResponse.then().assertThat().statusCode(400).body("success", is(false))
                .and().body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @Step("Создание заказа без авторизации и с ингридиентами")
    public void createOrderWithoutAuthAndIngredients() {
        Gson gson = new Gson();
        List<String> ingIds = new ArrayList<>();
        ingIds.add(ingredients.get(0).get_id());
        ingIds.add(ingredients.get(1).get_id());

        String body = "{\"ingredients\":" + gson.toJson(ingIds) + "}";

        Response createOrderResponse = given().contentType(ContentType.JSON).and()
                .body(body).and().post("/api/orders");
        createOrderResponse.then().assertThat().statusCode(200).body("success", is(true))
                .and().body("name", notNullValue())
                .and().body("order.number", notNullValue());
    }

    @Test
    @Step("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithNotAvailableId() {
        Gson gson = new Gson();
        List<String> ingIds = new ArrayList<>();
        ingIds.add(UUID.randomUUID().toString());
        ingIds.add(UUID.randomUUID().toString());

        String body = "{\"ingredients\":" + gson.toJson(ingIds) + "}";

        Response createOrderResponse = given().contentType(ContentType.JSON).and()
                .body(body).and().post("/api/orders");
        createOrderResponse.then().assertThat().statusCode(500);
    }

    @After
    public void deleteUserIfExists() {
        deleteUserIfExists(user);
    }

    @Step("Зарегестрировать пользователя")
    private Response registerUser(User user) {
        return given()
                .contentType(ContentType.JSON)
                .and()
                .body(user)
                .and()
                .post("/api/auth/register");
    }


    @Step("Удалить пользователя если зарегестрирован")
    private void deleteUserIfExists(User user) {
        Response responseLogin = given()
                .contentType(ContentType.JSON)
                .and()
                .body(user)
                .and()
                .post("/api/auth/login");
        if (responseLogin.statusCode() == 200) {
            String accessToken = responseLogin.then().extract().body().path("accessToken").toString();
            Response response = given().header("authorization", accessToken).contentType(ContentType.JSON)
                    .and()
                    .delete("/api/auth/user");
        }
    }

}

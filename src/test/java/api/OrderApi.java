package api;

import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import pojo.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static api.ApiPaths.INGREDIENTS;
import static io.restassured.RestAssured.given;

public class OrderApi extends AbstractApi {

    @Step("Получение заказa с токеном")
    public static Response getOrderByToken(String accessToken) {
        return given().spec(requestSpecification).header("authorization", accessToken).and()
                .get(ApiPaths.ORDERS);
    }

    @Step("Создание заказа с токеном и ингредиентами")
    public static Response createOrderByTokenAndIngredients(String accessToken, List<Ingredient> ingredientList) {
        return given().spec(requestSpecification).header("authorization", accessToken).and().body(getIngredientsBody(ingredientList))
                .post(ApiPaths.ORDERS);
    }

    @Step("Создание заказа с токеном и без ингредиентов")
    public static Response createOrderWithToken(String accessToken) {
        return given().spec(requestSpecification).header("authorization", accessToken).and()
                .post(ApiPaths.ORDERS);
    }

    @Step("Создание заказа без токена и c ингредиентами")
    public static Response createOrderWithIngredients(List<Ingredient> ingredientList) {
        return given().spec(requestSpecification).and().body(getIngredientsBody(ingredientList)).and()
                .post(ApiPaths.ORDERS);
    }

    @Step("Создание заказа без токена и без ингредиентов")
    public static Response createOrder() {
        return given().spec(requestSpecification).and()
                .post(ApiPaths.ORDERS);
    }

    @Step("Получение заказa без токена")
    public static Response getOrderWithoutToken() {
        return given().spec(requestSpecification).and()
                .get(ApiPaths.ORDERS);
    }

    @Step("Получение ингредиентов")
    public static Response getIngredients() {
        return given().spec(requestSpecification)
                .get(INGREDIENTS);
    }

    private static String getIngredientsBody(List<Ingredient> ingredientList){
        Gson gson = new Gson();
        List<String> ingIds = ingredientList.stream().map(Ingredient::get_id).collect(Collectors.toList());
        return "{\"ingredients\":" + gson.toJson(ingIds) + "}";
    }

}

package api;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import pojo.User;

import static api.ApiPaths.*;
import static io.restassured.RestAssured.given;

public class UserApi extends AbstractApi {

    @Step("Логин")
    public static Response login(User user) {
        return given().spec(requestSpecification).and().body(user)
                .and().post(LOGIN);
    }

    @Step("Удаление зарегистрированного пользователя")
    public static void deleteUser(Response loginResponse) {
        if (loginResponse.statusCode() == 200) {
            String accessToken = loginResponse.then().extract().body().path("accessToken").toString();
            given().spec(requestSpecification).header("authorization", accessToken)
                    .and().delete(AUTH);
        }
    }

    @Step("Попытка удаления пользователя если он есть в системе")
    public static void deleteUserIfExist(User user) {
        Response deleteResponse = login(user);
        deleteUser(deleteResponse);
    }

    @Step("Регистрация пользователя")
    public static Response register(User user) {
        return given().spec(requestSpecification)
                .and().body(user).and()
                .post(REGISTER);
    }

    @Step("Изменить данные пользователя с токеном")
    public static Response changeUserDataWithToken(User user, String accessToken) {
        return given().spec(requestSpecification).header("authorization", accessToken).and()
                .body(user).and().patch(AUTH);
    }

    @Step("Изменить данные пользователя без токена")
    public static Response changeUserDataWithoutToken(User user) {
        return given().spec(requestSpecification).and()
                .body(user).and().patch(AUTH);
    }

}

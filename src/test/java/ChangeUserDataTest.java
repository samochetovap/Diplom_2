import api.UserApi;
import builder.UserBuilder;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.User;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class ChangeUserDataTest {

    private User user;

    @Before
    public void setUp() {
        user = UserBuilder.generateUser();
    }

    @Test
    @DisplayName("Изменение данных пользователя с авторизацией")
    public void changeDataWithAuthorization() {
        Response responseRegister = UserApi.register(user);
        responseRegister.then().assertThat().statusCode(200).body("user.email", is(user.getEmail()));

        String accessToken = responseRegister.then().extract().body().path("accessToken").toString();

        String changedName = UUID.randomUUID().toString();
        String changedEmail = UUID.randomUUID() + "@somemail.com";

        User newUser = new User(changedEmail, "somepassword12345", changedName);

        Response response = UserApi.changeUserDataWithToken(newUser, accessToken);
        response.then().assertThat().statusCode(200).body("success", is(true))
                .and().body("user.email", is(newUser.getEmail()))
                .and().body("user.name", is(newUser.getName()));
        UserApi.deleteUserIfExist(newUser);
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    public void changeDataWithOutAuthorization() {
        Response responseRegister = UserApi.register(user);
        responseRegister.then().assertThat().statusCode(200).body("user.email", is(user.getEmail()));

        String changedName = UUID.randomUUID().toString();
        String changedEmail = UUID.randomUUID() + "@somemail.com";

        User newUser = new User(changedEmail, "somepassword12345", changedName);

        Response response = UserApi.changeUserDataWithoutToken(newUser);
        response.then().assertThat().statusCode(401).body("success", is(false))
                .and().body("message", is("You should be authorised"));
        UserApi.deleteUserIfExist(newUser);
    }

    @Test
    @DisplayName("Изменение на уже существующую почту")
    public void changeDataWithExistedEmail() {
        Response responseRegister = UserApi.register(user);
        responseRegister.then().assertThat().statusCode(200).body("user.email", is(user.getEmail()));
        String accessToken = responseRegister.then().extract().body().path("accessToken").toString();
        //пользователь с существующим емейлом
        User newUser = new User("123", "123", "123");
        Response response = UserApi.changeUserDataWithToken(newUser, accessToken);
        response.then().assertThat().statusCode(403).body("success", is(false));
    }

    @After
    public void deleteUserIfExists() {
        UserApi.deleteUserIfExist(user);
    }
}

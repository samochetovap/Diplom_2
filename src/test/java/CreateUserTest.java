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

public class CreateUserTest {

    private User user;

    @Before
    public void setUp() {
        user = UserBuilder.generateUser();
    }

    @Test
    @DisplayName("Тест на регистрацию пользователя")
    public void createUser() {
        UserApi.register(user).then().assertThat().statusCode(200).body("user.email", is(user.getEmail()));
    }

    @Test
    @DisplayName("Тест на регистрацию не уникального пользователя")
    public void createExistedUser() {
        UserApi.register(user).then().assertThat().statusCode(200).body("user.email", is(user.getEmail()));
        //создадим второй раз такого же пользователя
        UserApi.register(user).then().assertThat().statusCode(403).body("message", is("User already exists")).and().body("success", is(false));
    }

    @Test
    @DisplayName("Создать пользователя и не заполнить пароль")
    public void createWithoutPasswordUser() {
        user.setPassword(null);
        UserApi.register(user).then().assertThat().statusCode(403).body("message", is("Email, password and name are required fields")).and().body("success", is(false));
    }

    @Test
    @DisplayName("Создать пользователя и не заполнить почту")
    public void createWithoutMailUser() {
        user.setEmail(null);
        UserApi.register(user).then().assertThat().statusCode(403).body("message", is("Email, password and name are required fields")).and().body("success", is(false));
    }

    @After
    public void deleteUserIfExists() {
        UserApi.deleteUserIfExist(user);
    }

}

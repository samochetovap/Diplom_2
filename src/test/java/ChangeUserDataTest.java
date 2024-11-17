import io.qameta.allure.Step;
import io.restassured.RestAssured;
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
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
        user = new User("some12332112332111@gmail.com", "somepassword12345", "someUserName");

    }

    @Test
    @Step("Изменение данных пользователя с авторизацией")
    public void changeDataWithAuthorization() {
        Response responseRegister = registerUser(user);
        responseRegister.then().assertThat().statusCode(200).body("user.email", is(user.getEmail()));

        String accessToken = responseRegister.then().extract().body().path("accessToken").toString();

        String changedName = UUID.randomUUID().toString();
        String changedEmail = UUID.randomUUID() + "@somemail.com";

        User newUser = new User(changedEmail, "somepassword12345", changedName);

        Response response = given().header("authorization", accessToken).contentType(ContentType.JSON).and()
                .body(newUser).and().patch("/api/auth/user");
        response.then().assertThat().statusCode(200).body("success", is(true))
                .and().body("user.email", is(newUser.getEmail()))
                .and().body("user.name", is(newUser.getName()));
        deleteUserIfExists(newUser);
    }

    @Test
    @Step("Изменение данных пользователя без авторизации")
    public void changeDataWithOutAuthorization() {
        Response responseRegister = registerUser(user);
        responseRegister.then().assertThat().statusCode(200).body("user.email", is(user.getEmail()));

        String changedName = UUID.randomUUID().toString();
        String changedEmail = UUID.randomUUID() + "@somemail.com";

        User newUser = new User(changedEmail, "somepassword12345", changedName);

        Response response = given().contentType(ContentType.JSON).and()
                .body(newUser).and().patch("/api/auth/user");
        response.then().assertThat().statusCode(401).body("success", is(false))
                .and().body("message", is("You should be authorised"));
        deleteUserIfExists(newUser);
    }

    @Test
    @Step("Изменение на уже существующую почту")
    public void changeDataWithExistedEmail() {
        Response responseRegister = registerUser(user);
        responseRegister.then().assertThat().statusCode(200).body("user.email", is(user.getEmail()));
        String accessToken = responseRegister.then().extract().body().path("accessToken").toString();

        //пользователь с существующим емейлом
        User newUser = new User("123", "123", "123");

        Response response = given().header("authorization", accessToken).contentType(ContentType.JSON).and()
                .body(newUser).and().patch("/api/auth/user");
        response.then().assertThat().statusCode(403).body("success", is(false));
        deleteUserIfExists(newUser);
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

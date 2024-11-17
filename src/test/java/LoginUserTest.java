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
import static org.hamcrest.Matchers.notNullValue;

public class LoginUserTest {

    private User user;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
        user = new User("some12332112332111@gmail.com", "somepassword12345", "someUserName");

    }

    @Test
    @Step("Тест на логин под существующим пользователем")
    public void loginUser() {
        registerUser(user).then().assertThat().statusCode(200).body("user.email", is(user.getEmail()));
        given().contentType(ContentType.JSON).and()
                .body(user).and().post("/api/auth/login")
                .then().statusCode(200).body("success", is(true)).and().body("accessToken", notNullValue());
    }

    @Test
    @Step("Тест на логин под не существующим пользователем")
    public void loginWithRandomLoginAndPasswordUser() {
        user.setPassword(UUID.randomUUID().toString());
        user.setEmail(UUID.randomUUID().toString());
        given().contentType(ContentType.JSON).and()
                .body(user).and().post("/api/auth/login")
                .then().assertThat().statusCode(401).body("message", is("email or password are incorrect")).and().body("success", is(false));
    }

    @After
    public void deleteUserIfExists() {
        deleteUserIfExists(user);
    }

    @Step("Зарегестрировать пользователя")
    private Response registerUser(User user) {
        Response response = given()
                .contentType(ContentType.JSON)
                .and()
                .body(user)
                .and()
                .post("/api/auth/register");
        return response;
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

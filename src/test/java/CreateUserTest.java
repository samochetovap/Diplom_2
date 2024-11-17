import io.qameta.allure.Step;
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
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
        user = new User("some12332112332111@gmail.com", "somepassword12345", "someUserName");
    }

    @Test
    @Step("Тест на регистрацию пользователя")
    public void createUser() {
        registerUser(user).then().assertThat().statusCode(200).body("user.email", is(user.getEmail()));
    }

    @Test
    @Step("Тест на регистрацию не уникального пользователя")
    public void createExistedUser() {
        registerUser(user).then().assertThat().statusCode(200).body("user.email", is(user.getEmail()));
        //создадим второй раз такого же пользователя
        registerUser(user).then().assertThat().statusCode(403).body("message", is("User already exists")).and().body("success", is(false));
    }

    @Test
    @Step("Cоздать пользователя и не заполнить пароль")
    public void createWithoutPasswordUser() {
        user.setPassword(null);
        registerUser(user).then().assertThat().statusCode(403).body("message", is("Email, password and name are required fields")).and().body("success", is(false));
    }

    @Test
    @Step("Cоздать пользователя и не заполнить почту")
    public void createWithoutMailUser() {
        user.setEmail(null);
        registerUser(user).then().assertThat().statusCode(403).body("message", is("Email, password and name are required fields")).and().body("success", is(false));
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

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
import static org.hamcrest.Matchers.notNullValue;

public class GetOrderByUserTest {

    private User user;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
        user = new User("some12332112332111@gmail.com", "somepassword12345", "someUserName");
    }

    @Test
    @Step("Получение заказов конкретного пользователя с авторизацией")
    public void getOrderByAuthUser() {
        Response responseRegister = registerUser(user);
        String accessToken = responseRegister.then().extract().body().path("accessToken").toString();

        Response createOrderResponse = given().header("authorization", accessToken).contentType(ContentType.JSON).and()
                .get("/api/orders");
        createOrderResponse.then().assertThat().statusCode(200).body("success", is(true))
                .and().body("orders", notNullValue());
    }

    @Test
    @Step("Получение заказов без авторизации")
    public void getOrderByNotAuthUser() {
        Response createOrderResponse = given().contentType(ContentType.JSON).and()
                .get("/api/orders");
        createOrderResponse.then().assertThat().statusCode(401).body("success", is(false))
                .and().body("message", is("You should be authorised"));
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

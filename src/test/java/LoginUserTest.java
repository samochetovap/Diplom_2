import api.UserApi;
import builder.UserBuilder;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.User;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class LoginUserTest {

    private User user;

    @Before
    public void setUp() {
        user = UserBuilder.generateUser();
    }

    @Test
    @DisplayName("Тест на логин под существующим пользователем")
    public void loginUser() {
        UserApi.register(user).then().assertThat().statusCode(200).body("user.email", is(user.getEmail()));
        UserApi.login(user).then().statusCode(200).body("success", is(true)).and().body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Тест на логин под не существующим пользователем")
    public void loginWithRandomLoginAndPasswordUser() {
        user.setPassword(UUID.randomUUID().toString());
        user.setEmail(UUID.randomUUID().toString());
        UserApi.login(user).then().assertThat().statusCode(401).body("message", is("email or password are incorrect")).and().body("success", is(false));
    }

    @After
    public void deleteUserIfExists() {
        UserApi.deleteUserIfExist(user);
    }
}

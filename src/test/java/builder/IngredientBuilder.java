package builder;

import io.qameta.allure.Step;
import pojo.Ingredient;
import pojo.User;

import java.util.Random;
import java.util.UUID;

public class IngredientBuilder {

    @Step("Генерация несуществующего ингредиента")
    public static Ingredient generateIngredient(){
        Random random = new Random();
        Ingredient ingredient = new Ingredient();
        ingredient.set_id(UUID.randomUUID().toString());
        ingredient.setCalories(random.nextInt());
        ingredient.setCarbohydrates(random.nextInt());
        ingredient.setFat(random.nextInt());
        ingredient.setName(UUID.randomUUID().toString());
        ingredient.setPrice(random.nextInt());
        ingredient.setType(UUID.randomUUID().toString());
        ingredient.setProteins(random.nextInt());
        return ingredient;
    }

}

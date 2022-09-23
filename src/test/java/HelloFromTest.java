import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloFromTest {
    @Test
    public void testHelloFrom() {
        System.out.println("Hello from Elena Shapoval");
    }

    @Test
    public void testRestAssured() {
        Map<String, String> params = new HashMap<>();
        params.put("name","Helena");
        JsonPath response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();
        String answer = response.get("answer");
        if (answer == null){
            System.out.println("The key 'answer2' is absent");
        } else {
            System.out.println(answer);
        }
    }

    @ParameterizedTest
    @ValueSource (strings = {"", "Helena", "Sergey"})
    public void testHelloMethodWithName(String name) {
        Map<String, String> queryParams = new HashMap<>();

        if(name.length() > 0) {
            queryParams.put("name", name);
        }

        JsonPath response = RestAssured
                .given()
                .queryParams(queryParams)
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();

        String answer = response.get("answer");
        String expectedName = (name.length() > 0) ? name : "someone";
        assertEquals("Hello, " + expectedName, answer, "The answer is not expected");
    }
}

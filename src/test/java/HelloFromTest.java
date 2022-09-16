import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

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
        String answer = response.get("answer2");
        if (answer == null){
            System.out.println("The key 'answer2' is absent");
        } else {
            System.out.println(answer);
        }
    }
}

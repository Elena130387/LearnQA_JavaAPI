import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

public class HelloWorldTest {
    @Test
    public void testHelloWorld() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        System.out.println(response.asString());
        response.body().print();
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
    @Test
    public void testRestAssuredPost() {
        Response response = RestAssured
                .given()
                .body("param1=value1&param2=value2") // "сырой" способ передачи
                .post("https://playground.learnqa.ru/api/check_type")
                .andReturn();
        response.print();

        Response responseJSON = RestAssured
                .given()
                .body("{\"param3\":\"value3\",\"param4\":\"value4\"}") // JSON способ передачи
                .post("https://playground.learnqa.ru/api/check_type")
                .andReturn();
        responseJSON.print();

        // Map<> передает данные в JSON формате, как в примере для responseJSON
        Map<String, String> body = new HashMap<>();
        body.put("param5","value5");
        body.put("param6","value6");

        Response responseBody = RestAssured
                .given()
                .body(body) // JSON способ передачи
                .post("https://playground.learnqa.ru/api/check_type")
                .andReturn();
        responseBody.print();
    }
}

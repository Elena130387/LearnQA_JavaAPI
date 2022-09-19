import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.equalTo;
import java.util.HashMap;
import java.util.Map;
import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.notNullValue;


public class RestAssuredHWTest {
    @Test
    public void testJsonParsing() {
        JsonPath response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        String secondMessage = response.get("messages[1].message");
        System.out.println(secondMessage);
    }

    @Test
    public void testRedirect() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);
    }

    @Test
    public void testLongRedirect() {
        String URL = "https://playground.learnqa.ru/api/long_redirect";
        int responseStatus;
        do {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(URL)
                    .andReturn();
            responseStatus = response.statusCode();
            URL = response.getHeader("Location");
            if (URL != null) {
                System.out.println(URL);
            }
        } while (responseStatus != 200);
    }

    @Test
    public void testToken() throws InterruptedException {
        Map<String, String> headersToken = new HashMap<>();

        JsonPath response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String responseToken = response.get("token");
        int responseSeconds = response.get("seconds");
        responseSeconds = responseSeconds * 1000;

        headersToken.put("token",responseToken);
        RestAssured
                .given()
                .params(headersToken)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .then()
                .assertThat()
                .body("status", equalTo("Job is NOT ready"));

        sleep(responseSeconds);

        RestAssured
                .given()
                .params(headersToken)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .then()
                .assertThat()
                .body("status", equalTo("Job is ready"))
                .assertThat()
                .body("result", notNullValue());
    }
}



import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LectureThreeHWTest {
    @ParameterizedTest
    @ValueSource(strings = {"short phrase", "a phrase with more than fifteen characters"})
    public void testShortPhraseCheck(String phrase) {
        assertTrue(phrase.length() > 15,
                "Phrase length must be more than 15 characters");
    }

    @Test
    public void testHomeworkCookie() {
        Response response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        Map<String, String> cookies = response.getCookies();
        assertTrue(cookies.containsKey("HomeWork"),
                "Response doesn't have 'HomeWork' cookie");

        String homeworkCookie = response.getCookie("HomeWork");
        assertEquals(
                homeworkCookie,
                "hw_value",
                "Cookie HomeWork has wrong value");
    }

    @Test
    public void testHomeworkHeader() {
        Response response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();
        Headers responseHeaders = response.getHeaders();

        assertTrue(responseHeaders.hasHeaderWithName("x-secret-homework-header"),
                "Response doesn't have 'x-secret-homework-header' header");

        String homeworkHeader = response.getHeader("x-secret-homework-header");
        assertEquals(
                homeworkHeader,
                "Some secret value",
                "Header 'x-secret-homework-header' has wrong value");
    }
}

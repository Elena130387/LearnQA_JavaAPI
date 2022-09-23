import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LectureThreeHWTest {
    @ParameterizedTest
    @ValueSource(strings = {"short phrase","a phrase with more than fifteen characters"})
    public void testShortPhraseCheck(String phrase){
        assertTrue(phrase.length() > 15,
                "Phrase length must be more than 15 characters");
    }
}

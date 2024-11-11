package hadiz.hanaup_backend.service.beforeservice;

import hadiz.hanaup_backend.repository.TravelSpendingTestDTO.AnswerDTO;
import org.junit.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TravelSpendingTestServiceTest {

    private final TravelSpendingTestService service = new TravelSpendingTestService();

    @Test
    public void calculateMbtiTest() {
        List<AnswerDTO> answers = List.of(
                new AnswerDTO(1L, "I", -1, "E/I"),
                new AnswerDTO(2L, "I", -1, "E/I"),
                new AnswerDTO(3L, "E", 1, "E/I"),

                new AnswerDTO(4L, "T", -1, "F/T"),
                new AnswerDTO(5L, "F", 1, "F/T"),
                new AnswerDTO(6L, "T", -1, "F/T"),

                new AnswerDTO(7L, "ME", 1, "ME/PH"),
                new AnswerDTO(8L, "ME", 1, "ME/PH"),
                new AnswerDTO(9L, "PH", -1, "ME/PH"),

                new AnswerDTO(10L, "P", -1, "J/P"),
                new AnswerDTO(11L, "J", 1, "J/P"),
                new AnswerDTO(12L, "P", -1, "J/P")
        );

        String result = service.calculateMbti(answers);
        assertEquals("ITMEP", result); // 예상 결과에 따라 수정
    }
}

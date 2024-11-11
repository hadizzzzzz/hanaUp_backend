package hadiz.hanaup_backend.repository.TravelSpendingTestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnswerDTO {
    private Long questionId;
    private String answerText;
    private int score;
    private String indicator;
}

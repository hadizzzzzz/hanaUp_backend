package hadiz.hanaup_backend.repository.TravelSpendingTestDTO;

import lombok.Data;

@Data
public class AnswerDTO {
    private Long questionId;
    private String answerText;
    private int score;
    private String indicator;
}

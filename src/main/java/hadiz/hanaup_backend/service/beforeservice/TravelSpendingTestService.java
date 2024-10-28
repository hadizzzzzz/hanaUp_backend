package hadiz.hanaup_backend.service.beforeservice;

import hadiz.hanaup_backend.domain.before.TravelSpendingTest;
import hadiz.hanaup_backend.repository.TravelSpendingTestDTO.AnswerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TravelSpendingTestService {

    @Transactional
    public String calculateMbti(List<AnswerDTO> answers) {
        TravelSpendingTest test = new TravelSpendingTest();

        int eScore = 0, iScore = 0;
        int sScore = 0, nScore = 0;
        int tScore = 0, fScore = 0;
        int jScore = 0, pScore = 0;

        for (AnswerDTO answer : answers) {
            switch (answer.getIndicator()) {
                case "E/I":
                    if (answer.getScore() > 0) eScore += answer.getScore();
                    else iScore -= answer.getScore();
                    break;
                case "S/N":
                    if (answer.getScore() > 0) sScore += answer.getScore();
                    else nScore -= answer.getScore();
                    break;
                case "T/F":
                    if (answer.getScore() > 0) tScore += answer.getScore();
                    else fScore -= answer.getScore();
                    break;
                case "J/P":
                    if (answer.getScore() > 0) jScore += answer.getScore();
                    else pScore -= answer.getScore();
                    break;
            }
        }

        StringBuilder mbtiResult = new StringBuilder();
        mbtiResult.append(eScore >= iScore ? "E" : "I");
        mbtiResult.append(sScore >= nScore ? "S" : "N");
        mbtiResult.append(tScore >= fScore ? "T" : "F");
        mbtiResult.append(jScore >= pScore ? "J" : "P");

        return mbtiResult.toString();
    }
}



//controller 예시
/*@RestController
@RequestMapping("/api/mbti")
public class MbtiController {

    @Autowired
    private MbtiService mbtiService; //서비스

    @PostMapping("/result")
    public ResponseEntity<String> getMbtiResult(@RequestBody List<AnswerDTO> answers) {
        String mbtiResult = mbtiService.calculateMbti(answers);
        return ResponseEntity.ok(mbtiResult);
    }
}*/

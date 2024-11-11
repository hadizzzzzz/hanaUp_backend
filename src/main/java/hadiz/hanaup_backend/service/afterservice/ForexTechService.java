package hadiz.hanaup_backend.service.afterservice;

import hadiz.hanaup_backend.domain.User;
import hadiz.hanaup_backend.domain.after.ForeignCurrencyAccount;
import hadiz.hanaup_backend.domain.after.ForexTech;
import hadiz.hanaup_backend.repository.after.ForexTechRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ForexTechService {

    @Autowired
    private final ForexTechRepository forexTechRepository;

    @Transactional
    public void createForexTech(String currencyId, Long userId, double depositAmount) {

        ForexTech forexTech = new ForexTech();

        User user = new User();
        user.setUserID(userId);
        forexTech.setUser(user);

        forexTech.setCurrencyID(currencyId);
        forexTech.setBalance(depositAmount);
        forexTech.setActive(true);

        forexTechRepository.save(forexTech);
    }

    @Transactional
    public double autoRecharge(String currencyId, Long userId){

        // 1. 기존 사용자 ForexTech 정보를 가져오기
        ForexTech forexTech = forexTechRepository.findByUserId(userId);


        // 3. 기존 balance와 합산 (기존 하나머니 잔액 만큼 충전)
        double newBalance = forexTech.getBalance() * 2;

        // 4. ForexTech의 새로운 balance 업데이트
        forexTech.setBalance(newBalance);
        forexTechRepository.save(forexTech);

        // 5. 합산된 금액 반환
        return newBalance;
    }
}

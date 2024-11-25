package hadiz.hanaup_backend.service.afterservice;

import hadiz.hanaup_backend.domain.User;
import hadiz.hanaup_backend.domain.after.ForeignCurrencyAccount;
import hadiz.hanaup_backend.repository.after.ForeignCurrencyAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Transactional
@RequiredArgsConstructor
public class ForeignCurrencyAccountService {

    @Autowired
    private final ForeignCurrencyAccountRepository foreignCurrencyAccountRepository;


    // 저축 계좌 생성
    @Transactional
    public ForeignCurrencyAccount createAccount(Long userId, double depositAmount, int savingPeriod) {

        ForeignCurrencyAccount account = new ForeignCurrencyAccount();
        User user = new User();
        user.setUserID(userId);
        account.setAccountID(userId);
        account.setPeriod(savingPeriod);
        account.setFirstBalance(depositAmount);
        account.setUser(user);
        foreignCurrencyAccountRepository.save(account);

        return account;
    }

    // 계좌 해지 시 이자 및 원금 계산
    @Transactional
    public BigDecimal calculateInterest(Long userId, String country) {
        ForeignCurrencyAccount account = foreignCurrencyAccountRepository.findByUserId(userId);

        BigDecimal interestRate;
        if (country.equals("UK")){
            interestRate = BigDecimal.valueOf(0.05);
        }
        if (country.equals("Australia")){
            interestRate = BigDecimal.valueOf(0.0435);
        }
        if (country.equals("Philippines")){
            interestRate = BigDecimal.valueOf(0.0625);
        }
        if (country.equals("Europe")){
            interestRate = BigDecimal.valueOf(0.034);
        }
        if (country.equals("USA")){
            interestRate = BigDecimal.valueOf(0.05);
        }
        else {
            interestRate = BigDecimal.valueOf(0.035);
        }


        // 가입 기간에 따른 이자 계산
        long months = account.getPeriod();
        BigDecimal interest = BigDecimal.valueOf(account.getFirstBalance()) // Double을 BigDecimal로 변환
                .multiply(interestRate)
                .multiply(BigDecimal.valueOf(months / 12.0)) // 연이율을 기반으로 계산
                .setScale(2, RoundingMode.HALF_UP);


        account.setInterest(interest);
        account.setLastBalance(BigDecimal.valueOf(account.getFirstBalance()).add(interest));

        return BigDecimal.valueOf(account.getFirstBalance()).add(interest); // 원금 + 이자 반환
    }

    @Transactional
    public ForeignCurrencyAccount findOne(Long memberId){
        return foreignCurrencyAccountRepository.findByUserId(memberId);
    }

    @Transactional
    public void delete(ForeignCurrencyAccount account){
        foreignCurrencyAccountRepository.delete(account);
    }
}

package hadiz.hanaup_backend.service.afterservice;

import hadiz.hanaup_backend.domain.User;
import hadiz.hanaup_backend.domain.after.ForeignCurrencyAccount;
import hadiz.hanaup_backend.repository.UserRepository;
import hadiz.hanaup_backend.repository.after.ForeignCurrencyAccountRepository;
import hadiz.hanaup_backend.repository.before.TravelSpendingTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class ForeignCurrencyAccountService {

    @Autowired
    private final ForeignCurrencyAccountRepository foreignCurrencyAccountRepository;

    @Autowired
    private static final BigDecimal INTEREST_RATE = BigDecimal.valueOf(0.035); // 연이자율 3.5%

    // 저축 계좌 생성
    @Transactional
    public ForeignCurrencyAccount createAccount(Long userId, double depositAmount, int savingPeriod) {

        ForeignCurrencyAccount account = new ForeignCurrencyAccount();
        User user = new User();
        user.setUserID(userId);
        account.setAccountID(userId);
        // account.setCurrencyID(currencyId);
        account.setPeriod(savingPeriod);
        account.setFirstBalance(depositAmount);
        account.setActive(true);
        account.setUser(user);
        foreignCurrencyAccountRepository.save(account);

        return account;
    }

    // 계좌 해지 시 이자 및 원금 계산
    @Transactional
    public BigDecimal calculateInterest(Long userId) {
        ForeignCurrencyAccount account = foreignCurrencyAccountRepository.findByUserId(userId);

        if (!account.isActive()) {
            throw new IllegalStateException("Account is already closed");
        }

        // 가입 기간에 따른 이자 계산
        long months = account.getPeriod();
        BigDecimal interest = BigDecimal.valueOf(account.getFirstBalance()) // Double을 BigDecimal로 변환
                .multiply(INTEREST_RATE)
                .multiply(BigDecimal.valueOf(months / 12.0)) // 연이율을 기반으로 계산
                .setScale(2, RoundingMode.HALF_UP);

        // 계좌 상태 비활성화
        account.setActive(false);
        account.setInterest(interest);
        account.setLastBalance(BigDecimal.valueOf(account.getFirstBalance()).add(interest));
        foreignCurrencyAccountRepository.save(account);

        return BigDecimal.valueOf(account.getFirstBalance()).add(interest); // 원금 + 이자 반환
    }

    public ForeignCurrencyAccount findOne(Long memberId){
        return foreignCurrencyAccountRepository.findByUserId(memberId);
    }
}

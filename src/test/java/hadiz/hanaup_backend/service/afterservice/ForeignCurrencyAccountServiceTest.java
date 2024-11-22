package hadiz.hanaup_backend.service.afterservice;

import hadiz.hanaup_backend.domain.User;
import hadiz.hanaup_backend.domain.after.ForeignCurrencyAccount;
import hadiz.hanaup_backend.repository.UserRepository;
import hadiz.hanaup_backend.repository.after.ForeignCurrencyAccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

@SpringBootTest
@Transactional
@RunWith(SpringRunner.class)
public class ForeignCurrencyAccountServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    private ForeignCurrencyAccountRepository foreignCurrencyAccountRepository;

    @Autowired
    private ForeignCurrencyAccountService foreignCurrencyAccountService;

    @Autowired
    UserRepository userRepository;

    @Test
    @Transactional
    @Rollback(value = false)
    public void testCreateAccount() {

        // 유저 설정
        User user = new User();
        user.setName("Test User");
        //user.setUserID();
        userRepository.save(user);

        // 계좌 생성 테스트
        //foreignCurrencyAccountService.createAccount("USD", user.getUserID(), 1000.0, 6);

        //ForeignCurrencyAccount account = foreignCurrencyAccountRepository.findByUserId(user.getUserID());

        //BigDecimal result = foreignCurrencyAccountService.calculateInterest(user.getUserID());
        //System.out.println(result);
    }



}

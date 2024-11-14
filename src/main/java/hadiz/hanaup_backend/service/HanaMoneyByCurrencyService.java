package hadiz.hanaup_backend.service;

import hadiz.hanaup_backend.domain.HanaMoneyByCurrency;
import hadiz.hanaup_backend.domain.User;
import hadiz.hanaup_backend.repository.HanaMoneyByCurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HanaMoneyByCurrencyService {

    private final HanaMoneyByCurrencyRepository repository;

    // 특정 사용자에 대한 데이터 조회
    public List<HanaMoneyByCurrency> getAllHanaMoneyByUser(User user) {
        return repository.findAllByUser(user);
    }

    // 커스텀 저장 메서드
    public void saveOrUpdateHanaMoney(HanaMoneyByCurrency hanaMoneyByCurrency) {
        repository.saveCustom(hanaMoneyByCurrency);
    }
}

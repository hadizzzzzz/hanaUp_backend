package hadiz.hanaup_backend.service;

import hadiz.hanaup_backend.domain.HanaMoneyByCurrency;
import hadiz.hanaup_backend.domain.User;
import hadiz.hanaup_backend.repository.HanaMoneyByCurrencyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HanaMoneyByCurrencyService {

    private final HanaMoneyByCurrencyRepository repository;

    // 특정 사용자에 대한 데이터 조회
    @Transactional
    public List<HanaMoneyByCurrency> getAllHanaMoneyByUser(User user) {
        return repository.findAllByUser(user);
    }

    // 특정 국가의 하나머니 데이터 조회
    @Transactional
    public HanaMoneyByCurrency getHanaMoneyByCountry(User user, String country) {
        List<HanaMoneyByCurrency> hanaMoneyList = repository.findAllByUser(user);
        return repository.findHanaMoneyByCountry(hanaMoneyList, country);
    }

    // 커스텀 저장 메서드
    @Transactional
    public void saveOrUpdateHanaMoney(HanaMoneyByCurrency hanaMoneyByCurrency) {
        repository.saveCustom(hanaMoneyByCurrency);
    }

    // 특정 하나머니 데이터 삭제
    @Transactional
    public void deleteHanaMoney(HanaMoneyByCurrency hanaMoneyByCurrency) {
        repository.delete(hanaMoneyByCurrency);
    }

    // 특정 사용자와 국가의 하나머니 데이터 삭제
    @Transactional
    public void deleteHanaMoneyByCountryAndUser(User user, String country) {
        HanaMoneyByCurrency hanaMoney = getHanaMoneyByCountry(user, country);
        if (hanaMoney != null) {
            repository.delete(hanaMoney);
        }
    }

    // 사용자에 대한 모든 하나머니 데이터 삭제
    @Transactional
    public void deleteAllHanaMoneyByUser(User user) {
        List<HanaMoneyByCurrency> hanaMoneyList = repository.findAllByUser(user);
        hanaMoneyList.forEach(repository::delete);
    }
}

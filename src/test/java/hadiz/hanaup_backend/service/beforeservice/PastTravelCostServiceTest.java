package hadiz.hanaup_backend.service.beforeservice;

import jakarta.transaction.Transactional;

import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@Transactional
class PastTravelCostServiceTest {

    /*@Autowired
    EntityManager em;

    @Autowired
    CPIDataService cpiDataService;

    @Autowired
    TravelLogRepository travelLogRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PastTravelCostPredictionRepository predictionRepository;

    @Autowired
    PastTravelCostService pastTravelCostService;


    @Test
    @Transactional
    @Rollback(value = false)
    void 과거결제기록으로미래비용예측() {

        ///////////////////// Given

        // 유저 설정
        User user = new User();
        user.setName("Test User");
        //user.setUserID(1L);
        userRepository.save(user);

        // TravelLog 설정 (과거여행)
        TravelLog travelLog = new TravelLog();
        travelLog.setUser(user);
        travelLog.setDestination("korea");
        travelLog.setDuration(7);
        travelLog.setTotalSpent(1400000.0);

        travelLogRepository.save(travelLog);

        //미래여행
        PastTravelCostPrediction pastTravelCostPrediction = new PastTravelCostPrediction();
        pastTravelCostPrediction.setUser(user);
        pastTravelCostPrediction.setCountry("usa");
        pastTravelCostPrediction.setTravelDuration(6);
        predictionRepository.save(pastTravelCostPrediction);
        

        /////////////////// Then

        String country = pastTravelCostPrediction.getCountry();
        int duration = pastTravelCostPrediction.getTravelDuration();

        double predictedCost = pastTravelCostService.predictTravelCost(user.getUserID(), travelLog.getLogID(), country, duration);

        System.out.println("predictedCost = " + predictedCost);


        Double expectedCost = (1400000.0 / travelLog.getDuration()) * (250.0 / 90.0) * pastTravelCostPrediction.getTravelDuration();

        System.out.println("expectedCost = " + expectedCost);



    }*/

}





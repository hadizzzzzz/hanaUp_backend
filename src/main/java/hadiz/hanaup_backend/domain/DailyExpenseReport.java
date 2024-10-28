package hadiz.hanaup_backend.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "daily_expense_report")
public class DailyExpenseReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportID;

    @Column(name = "day_number")  // 'day'는 H2 예약어이므로 변경
    private int day;

    private Double foodExpense;
    private Double transportExpense;
    private Double shoppingExpense;
    private Double activityExpense;
    private Double totalSpent;
    private Double savedFees;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_log_id")
    private TravelLog travelLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Getters and Setters
}



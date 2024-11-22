package hadiz.hanaup_backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "daily_expense_report")
@Getter
@Setter
public class DailyExpenseReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportID;

    private String country;

    @Column(name = "day_number")  // 'day'는 H2 예약어이므로 변경
    private int day;

    private int foodExpense;
    private int transportExpense;
    private int hotelExpense;
    private int shoppingExpense;
    private int activityExpense;

    private int totalSpent;
    private int savedFees;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


}



package hadiz.hanaup_backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "travel_log")
public class TravelLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logID;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String destination;
    private Double totalSpent;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "travelLog", cascade = CascadeType.ALL)
    private List<DailyExpenseReport> dailyReports = new ArrayList<>();

    // Getters and Setters
}


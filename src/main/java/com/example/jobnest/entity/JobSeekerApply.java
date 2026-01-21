package com.example.jobnest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "job_seeker_apply")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerApply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int applyId;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private Date applyDate;

    @Column(length = 2000)
    private String coverLetter;

    @Column(nullable = false, length = 50)
    private String status;

    @PrePersist
    protected void onCreate() {
        if (applyDate == null) {
            applyDate = new Date();
        }
        if (status == null || status.isEmpty()) {
            status = "Pending";
        }
    }

    @Override
    public String toString() {
        return "JobSeekerApply{" +
                "applyId=" + applyId +
                ", jobId=" + (job != null ? job.getJobId() : "null") +
                ", userId=" + (user != null ? user.getUserId() : "null") +
                ", applyDate=" + applyDate +
                ", status='" + status + '\'' +
                '}';
    }
}


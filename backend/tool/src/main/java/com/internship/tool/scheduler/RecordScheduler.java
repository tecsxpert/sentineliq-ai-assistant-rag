package com.internship.tool.scheduler;

import com.internship.tool.entity.Record;
import com.internship.tool.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class RecordScheduler {

    @Autowired
    private RecordRepository recordRepository;

    // ✅ 1. DAILY JOB → overdue records
    @Scheduled(cron = "0 0 9 * * ?") // every day 9 AM
    public void checkOverdueRecords() {
        LocalDate today = LocalDate.now();

        List<Record> overdue = recordRepository.findByDueDateBeforeAndStatusNot(
                today, "COMPLETED"
        );

        System.out.println("Overdue Records: " + overdue.size());

        for (Record r : overdue) {
            System.out.println("Overdue: " + r.getTitle());
        }
    }

    // ✅ 2. 7-DAY ALERT JOB
    @Scheduled(cron = "0 0 10 * * ?") // every day 10 AM
    public void upcomingDeadlineAlert() {
        LocalDate today = LocalDate.now();
        LocalDate next7Days = today.plusDays(7);

        List<Record> upcoming = recordRepository
                .findByDueDateBetween(today, next7Days);

        System.out.println("Upcoming Records: " + upcoming.size());

        for (Record r : upcoming) {
            System.out.println("Upcoming: " + r.getTitle());
        }
    }

    // ✅ 3. WEEKLY SUMMARY
    @Scheduled(cron = "0 0 11 ? * MON") // every Monday 11 AM
    public void weeklySummary() {
        long total = recordRepository.count();

        System.out.println("Weekly Summary:");
        System.out.println("Total Records: " + total);
    }
}
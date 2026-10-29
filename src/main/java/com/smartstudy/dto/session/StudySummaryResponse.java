package com.smartstudy.dto.session;

public class StudySummaryResponse {

    private int todayMinutes;
    private int weekMinutes;
    private int totalSessions;
    private int completedSessions;
    private int totalBreakMinutes;

    public StudySummaryResponse() {
    }

    public StudySummaryResponse(int todayMinutes, int weekMinutes, int totalSessions,
                                int completedSessions, int totalBreakMinutes) {
        this.todayMinutes = todayMinutes;
        this.weekMinutes = weekMinutes;
        this.totalSessions = totalSessions;
        this.completedSessions = completedSessions;
        this.totalBreakMinutes = totalBreakMinutes;
    }

    public int getTodayMinutes() {
        return todayMinutes;
    }

    public void setTodayMinutes(int todayMinutes) {
        this.todayMinutes = todayMinutes;
    }

    public int getWeekMinutes() {
        return weekMinutes;
    }

    public void setWeekMinutes(int weekMinutes) {
        this.weekMinutes = weekMinutes;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }

    public int getCompletedSessions() {
        return completedSessions;
    }

    public void setCompletedSessions(int completedSessions) {
        this.completedSessions = completedSessions;
    }

    public int getTotalBreakMinutes() {
        return totalBreakMinutes;
    }

    public void setTotalBreakMinutes(int totalBreakMinutes) {
        this.totalBreakMinutes = totalBreakMinutes;
    }
}

package com.example.jobnest.services;

import com.example.jobnest.dto.response.AdminDashboardDTO;
import com.example.jobnest.dto.response.AdminReportsDTO;

/**
 * Service for admin dashboard and reports.
 */
public interface AdminReportService {

    /**
     * Get basic admin dashboard statistics.
     *
     * @return Admin dashboard data
     */
    AdminDashboardDTO getDashboardStats();

    /**
     * Get detailed admin reports with analytics.
     *
     * @return Detailed reports data
     */
    AdminReportsDTO getDetailedReports();
}

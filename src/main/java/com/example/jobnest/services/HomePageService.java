package com.example.jobnest.services;

import com.example.jobnest.dto.response.HomePageDataDTO;

/**
 * Service for homepage data.
 */
public interface HomePageService {

    /**
     * Get homepage data including featured jobs and statistics.
     *
     * @return Homepage data
     */
    HomePageDataDTO getHomePageData();
}

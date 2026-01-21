/**
 * Global Theme Toggle Script
 * Handles dark/light mode switching across the entire application
 * Persists theme preference in localStorage
 */

(function() {
    'use strict';
    
    const themeToggle = document.getElementById('themeToggle');
    const themeIcon = document.getElementById('themeIcon');
    const html = document.documentElement;
    
    // Get saved theme or default to light
    const savedTheme = localStorage.getItem('theme') || 'light';
    
    // Apply theme immediately (already done in layout.html, but ensure it's set)
    html.setAttribute('data-bs-theme', savedTheme);
    updateIcon(savedTheme);
    
    // Toggle theme on button click
    if (themeToggle) {
        themeToggle.addEventListener('click', function() {
            const currentTheme = html.getAttribute('data-bs-theme');
            const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
            
            // Apply new theme
            html.setAttribute('data-bs-theme', newTheme);
            
            // Save to localStorage
            localStorage.setItem('theme', newTheme);
            
            // Update icon
            updateIcon(newTheme);
            
            // Dispatch custom event for any components that need to react
            window.dispatchEvent(new CustomEvent('themeChanged', { 
                detail: { theme: newTheme } 
            }));
        });
    }
    
    /**
     * Update the theme icon based on current theme
     */
    function updateIcon(theme) {
        if (themeIcon) {
            if (theme === 'dark') {
                themeIcon.className = 'fa fa-sun';
            } else {
                themeIcon.className = 'fa fa-moon';
            }
        }
    }
    
    /**
     * Listen for theme changes from other pages/components
     */
    window.addEventListener('storage', function(e) {
        if (e.key === 'theme') {
            const newTheme = e.newValue || 'light';
            html.setAttribute('data-bs-theme', newTheme);
            updateIcon(newTheme);
        }
    });
    
    // Expose theme getter for other scripts
    window.getTheme = function() {
        return html.getAttribute('data-bs-theme') || 'light';
    };
    
    // Expose theme setter for other scripts
    window.setTheme = function(theme) {
        if (theme === 'dark' || theme === 'light') {
            html.setAttribute('data-bs-theme', theme);
            localStorage.setItem('theme', theme);
            updateIcon(theme);
            window.dispatchEvent(new CustomEvent('themeChanged', { 
                detail: { theme: theme } 
            }));
        }
    };
})();

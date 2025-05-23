refreshStatsBtn.addEventListener('click', () => {
    loadingIndicator.style.display = 'block';

    // Make API call to retrieve updated statistics
    fetchWithAuthToken('/api/admin/dashboard/stats')
        .then(response => response.json())
        .then(data => {
            totalCampaignsElement.textContent = data.totalCampaigns === null ? 0 : data.totalCampaigns;
            totalDonationsElement.textContent = data.totalDonations === null ? 0 : data.totalDonations;
            totalUsersElement.textContent = data.totalUsers === null ? 0 : data.totalUsers;
            totalFundRaisedElement.textContent = data.totalFundRaised === null ? 0 : data.totalFundRaised;
            loadingIndicator.style.display = 'none';
        })
        .catch(error => console.error('Error refreshing statistics:', error));
    loadingIndicator.style.display = 'none';
});

function handleLogout(e) {
    e.preventDefault();
    fetchWithAuthToken('/api/auth/logout', {
        method: 'POST'
    })
    .then((response) => {
        if (response.ok) {
            localStorage.removeItem('jwtToken');
            window.location.href = '/';
        }
        else {
            console.error('Logout failed');
        }
    })
    .catch((error) => console.error('Error:', error));
}
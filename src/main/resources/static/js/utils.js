function fetchWithAuthToken(url, options = {}) {
    const token = localStorage.getItem('jwtToken');
    if (token) {
        options.headers = options.headers || {};
        options.headers.Authorization = `Bearer ${token}`;
    }
    return fetch(url, options);
}

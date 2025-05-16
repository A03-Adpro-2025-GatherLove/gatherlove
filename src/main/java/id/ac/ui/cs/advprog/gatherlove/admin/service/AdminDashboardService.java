package id.ac.ui.cs.advprog.gatherlove.admin.service;

import java.util.UUID;

import id.ac.ui.cs.advprog.gatherlove.admin.dto.Stats;

public interface AdminDashboardService {
    Stats getStats();
    void deleteUser(UUID user_id);
}

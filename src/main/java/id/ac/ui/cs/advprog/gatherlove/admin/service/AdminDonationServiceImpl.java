package id.ac.ui.cs.advprog.gatherlove.admin.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.gatherlove.donation.model.DonationDummy;

@Service
public class AdminDonationServiceImpl implements AdminDonationService {
    @Override
    public List<DonationDummy> getDonationHistory() {
        // dummy data
        List<DonationDummy> donationHistory = new ArrayList<>();
        donationHistory.add(new DonationDummy(1L, 100L, "John Doe"));
        donationHistory.add(new DonationDummy(2L, 200L, "Jane Doe"));
        donationHistory.add(new DonationDummy(3L, 300L, "Bob Smith"));
        return donationHistory;
    }
}

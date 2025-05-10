package id.ac.ui.cs.advprog.gatherlove.admin.service;

import java.util.List;

import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;

public interface AdminDonationService {
    List<Donation> getDonationHistory();
}

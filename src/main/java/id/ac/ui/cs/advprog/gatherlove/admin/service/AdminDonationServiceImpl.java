package id.ac.ui.cs.advprog.gatherlove.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.repository.DonationRepository;

@Service
public class AdminDonationServiceImpl implements AdminDonationService {

    @Autowired
    private DonationRepository donationRepository;

    @Override
    public List<Donation> getDonationHistory() {
        List<Donation> donations = donationRepository.findAll();
        return donations;
    }
}

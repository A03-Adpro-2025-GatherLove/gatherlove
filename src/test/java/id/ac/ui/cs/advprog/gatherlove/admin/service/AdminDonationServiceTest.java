package id.ac.ui.cs.advprog.gatherlove.admin.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import id.ac.ui.cs.advprog.gatherlove.donation.model.DonationDummy;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AdminDonationServiceTest {

    @Autowired
    private AdminDonationService adminDonationService;

    @Test
    public void testGetDonationHistory() {
        List<DonationDummy> donationHistory = adminDonationService.getDonationHistory();
        assertNotNull(donationHistory);
        assertEquals(3, donationHistory.size()); // dummy data dengan 3 transaksi
    }
}
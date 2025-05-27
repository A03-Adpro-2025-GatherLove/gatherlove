package id.ac.ui.cs.advprog.gatherlove.donation.controller;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import id.ac.ui.cs.advprog.gatherlove.authentication.security.services.UserDetailsImpl;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.CampaignStatus;
import id.ac.ui.cs.advprog.gatherlove.campaign.service.CampaignService;
import id.ac.ui.cs.advprog.gatherlove.donation.command.CommandInvoker; // Tambahkan
import id.ac.ui.cs.advprog.gatherlove.donation.command.MakeDonationCommand; // Tambahkan
import id.ac.ui.cs.advprog.gatherlove.donation.dto.CreateDonationRequest;
import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.service.DonationService;

import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor; // Hapus jika menggunakan constructor injection manual
import org.springframework.beans.factory.annotation.Autowired; // Tambahkan
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/donate")
public class DonationPageController {

    private final CampaignService campaignService;
    private final DonationService donationService;
    private final CommandInvoker commandInvoker;

    @Autowired
    public DonationPageController(CampaignService campaignService, DonationService donationService, CommandInvoker commandInvoker) {
        this.campaignService = campaignService;
        this.donationService = donationService;
        this.commandInvoker = commandInvoker;
    }

    @GetMapping("/browse")
    public String browseCampaignsForDonation(Model model) {
        model.addAttribute("approvedCampaigns", campaignService.getCampaignsByStatus(CampaignStatus.APPROVED));
        model.addAttribute("completedCampaigns", campaignService.getCampaignsByStatus(CampaignStatus.COMPLETED));
        return "donation/browse-campaigns";
    }

    @GetMapping("/create/{campaignId}")
    public String showCreateDonationForm(@PathVariable("campaignId") String campaignId, Model model,
                                         @AuthenticationPrincipal UserDetailsImpl currentUserDetails) { // Tambahkan @AuthenticationPrincipal
        if (currentUserDetails == null) {
            return "redirect:/login?redirect=/donate/create/" + campaignId; // Contoh redirect ke login
        }

        try {
            Campaign campaign = campaignService.getCampaignById(campaignId);
            if (campaign == null || campaign.getStatus() != CampaignStatus.APPROVED) {
                model.addAttribute("errorMessage", "Campaign is not available for donation or does not exist.");
                return "error/general-error"; // Atau view error yang sesuai
            }

            CreateDonationRequest donationRequest = new CreateDonationRequest();
            donationRequest.setCampaignId(campaignId);

            model.addAttribute("campaign", campaign);
            model.addAttribute("createDonationRequest", donationRequest);
            // Anda bisa menambahkan informasi user ke model jika diperlukan di view
            // model.addAttribute("currentUser", currentUserDetails.getUser());
            return "donation/create-donation-form";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error fetching campaign details: " + e.getMessage());
            return "error/general-error"; // Atau view error yang sesuai
        }
    }


    @PostMapping("/create")
    public String processCreateDonation(@Valid @ModelAttribute("createDonationRequest") CreateDonationRequest request,
                                        BindingResult bindingResult,
                                        @AuthenticationPrincipal UserDetailsImpl currentUserDetails, // Menggunakan @AuthenticationPrincipal
                                        Model model,
                                        RedirectAttributes redirectAttributes) {

        if (currentUserDetails == null) {
            // Handle jika user tidak terautentikasi saat POST
            redirectAttributes.addFlashAttribute("errorMessage", "Authentication required to make a donation.");
            // Mungkin redirect ke halaman login atau halaman browse dengan pesan
            return "redirect:/donate/browse";
        }

        Campaign campaign = null;
        if (request.getCampaignId() != null && !request.getCampaignId().isEmpty()) {
            try {
                campaign = campaignService.getCampaignById(request.getCampaignId());
            } catch (Exception e) {
                System.err.println("Error fetching campaign for form re-render on validation error: " + e.getMessage());
                // Jika campaign tidak valid, redirect lebih baik daripada error page saat binding error
            }
        }

        if (bindingResult.hasErrors()) {
            if (campaign != null) {
                model.addAttribute("campaign", campaign);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid campaign ID provided with the donation form.");
                return "redirect:/donate/browse";
            }
            model.addAttribute("createDonationRequest", request);
            return "donation/create-donation-form";
        }

        try {
            UUID donorId = currentUserDetails.getId(); // Mendapatkan ID dari UserDetailsImpl

            MakeDonationCommand makeDonationCommand = new MakeDonationCommand(
                    donationService,
                    donorId,
                    request.getCampaignId(),
                    request.getAmount(),
                    request.getMessage()
            );

            CompletableFuture<Donation> futureDonation = commandInvoker.executeCommand(makeDonationCommand);
            futureDonation.get(); // Tunggu hasil

            redirectAttributes.addFlashAttribute("successMessage", "Thank you for your generous donation!");
            return "redirect:/donate/browse";

        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            String errorMessage = "Donation failed: ";
            if (cause instanceof IllegalArgumentException) {
                errorMessage += cause.getMessage();
            } else if (cause instanceof RuntimeException && cause.getMessage() != null && cause.getMessage().contains("Saldo tidak mencukupi")) {
                errorMessage = "Donation failed: Insufficient wallet balance.";
            } else {
                errorMessage += (cause != null ? cause.getMessage() : "An unknown error occurred during donation processing.");
            }
            model.addAttribute("errorMessage", errorMessage);
            if (campaign != null) {
                model.addAttribute("campaign", campaign);
            }
            model.addAttribute("createDonationRequest", request);
            return "donation/create-donation-form";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            model.addAttribute("errorMessage", "Donation process was interrupted.");
            if (campaign != null) {
                model.addAttribute("campaign", campaign);
            }
            model.addAttribute("createDonationRequest", request);
            return "donation/create-donation-form";
        }
        // Menghapus catch IllegalStateException karena getCurrentUserId() tidak lagi digunakan
        catch (Exception e) {
            model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            if (campaign != null) {
                model.addAttribute("campaign", campaign);
            }
            model.addAttribute("createDonationRequest", request);
            return "donation/create-donation-form";
        }
    }
}
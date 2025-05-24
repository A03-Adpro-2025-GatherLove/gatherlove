package id.ac.ui.cs.advprog.gatherlove.donation.controller;

import id.ac.ui.cs.advprog.gatherlove.authentication.model.UserEntity;
import id.ac.ui.cs.advprog.gatherlove.authentication.security.services.UserDetailsImpl;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.Campaign;
import id.ac.ui.cs.advprog.gatherlove.campaign.model.CampaignStatus;
import id.ac.ui.cs.advprog.gatherlove.campaign.service.CampaignService;
import id.ac.ui.cs.advprog.gatherlove.donation.dto.CreateDonationRequest;
import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.service.DonationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import static java.lang.System.out;

@Controller
@RequestMapping("/donate") // Base path untuk UI donasi
@RequiredArgsConstructor
public class DonationPageController {

    private final CampaignService campaignService;
    private final DonationService donationService;

    private UUID getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var principal = (UserDetailsImpl) auth.getPrincipal();
        return principal.getId();
    }

    @GetMapping("/browse")
    public String browseCampaignsForDonation(Model model) {
        model.addAttribute("approvedCampaigns", campaignService.getCampaignsByStatus(CampaignStatus.APPROVED));
        model.addAttribute("completedCampaigns", campaignService.getCampaignsByStatus(CampaignStatus.COMPLETED));
        return "donation/browse-campaigns";
    }

    @GetMapping("/create/{campaignId}")
    public String showCreateDonationForm(@PathVariable String campaignId, Model model, @AuthenticationPrincipal UserEntity currentUser) {
//        out.println(currentUser.getId());
//        if (currentUser == null) {
//            return "redirect:/login?redirect=/donate/create/" + campaignId;
//        }
        try {
            Campaign campaign = campaignService.getCampaignById(campaignId);
            if (campaign == null || campaign.getStatus() != CampaignStatus.APPROVED) {
                model.addAttribute("errorMessage", "Campaign is not available for donation or does not exist.");
                return "error/general-error";
            }

            CreateDonationRequest donationRequest = new CreateDonationRequest();
            donationRequest.setCampaignId(campaignId);

            model.addAttribute("campaign", campaign);
            model.addAttribute("createDonationRequest", donationRequest);
            return "donation/create-donation-form";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error fetching campaign details: " + e.getMessage());
            return "error/general-error";
        }
    }


    @PostMapping("/create")
    public String processCreateDonation(@Valid @ModelAttribute("createDonationRequest") CreateDonationRequest request,
                                        BindingResult bindingResult,
                                        @AuthenticationPrincipal UserEntity currentUser,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {

//        if (currentUser == null) {
//            // PERUBAHAN DI SINI: Mengarahkan ke /login bukan /auth/login
//            // Coba tambahkan campaignId ke redirect jika request.getCampaignId() tidak null
//            String redirectPath = (request.getCampaignId() != null && !request.getCampaignId().isEmpty())
//                    ? "/donate/create/" + request.getCampaignId()
//                    : "/donate/browse"; // Fallback jika campaignId tidak ada
//            return "redirect:/login?redirect=" + redirectPath;
//        }

        Campaign campaign = null;
        if (request.getCampaignId() != null && !request.getCampaignId().isEmpty()) {
            try {
                campaign = campaignService.getCampaignById(request.getCampaignId());
            } catch (Exception e) {
                System.err.println("Error fetching campaign for form re-render on validation error: " + e.getMessage());
            }
        }

        if (bindingResult.hasErrors()) {
            if (campaign != null) {
                model.addAttribute("campaign", campaign);
            } else {
                // Jika campaign tidak bisa diambil (misal ID salah di form hidden),
                // mungkin lebih baik redirect ke browse dengan pesan error
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid campaign ID provided.");
                return "redirect:/donate/browse";
            }
            model.addAttribute("createDonationRequest", request); // Tambahkan kembali request agar field terisi
            return "donation/create-donation-form";
        }

        try {
//
            UUID donorId = getCurrentUserId();
            CompletableFuture<Donation> futureDonation =
                    donationService.createDonation(donorId, request.getCampaignId(), request.getAmount(), request.getMessage());

            futureDonation.get(); // Tunggu operasi async selesai (blocking)

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
            model.addAttribute("createDonationRequest", request); // Tambahkan kembali request agar field terisi
            return "donation/create-donation-form";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            model.addAttribute("errorMessage", "Donation process was interrupted.");
            if (campaign != null) {
                model.addAttribute("campaign", campaign);
            }
            model.addAttribute("createDonationRequest", request); // Tambahkan kembali request agar field terisi
            return "donation/create-donation-form";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            if (campaign != null) {
                model.addAttribute("campaign", campaign);
            }
            model.addAttribute("createDonationRequest", request); // Tambahkan kembali request agar field terisi
            return "donation/create-donation-form";
        }
    }
}
package id.ac.ui.cs.advprog.gatherlove.donation.controller;

import id.ac.ui.cs.advprog.gatherlove.donation.dto.CreateDonationRequest;
import id.ac.ui.cs.advprog.gatherlove.donation.dto.DonationResponse;
import id.ac.ui.cs.advprog.gatherlove.donation.exception.DonationNotFoundException;
import id.ac.ui.cs.advprog.gatherlove.donation.exception.UnauthorizedException;
import id.ac.ui.cs.advprog.gatherlove.donation.model.Donation;
import id.ac.ui.cs.advprog.gatherlove.donation.service.DonationService;
import id.ac.ui.cs.advprog.gatherlove.donation.command.MakeDonationCommand;
import id.ac.ui.cs.advprog.gatherlove.donation.command.RemoveDonationMessageCommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import id.ac.ui.cs.advprog.gatherlove.authentication.security.services.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
public class DonationController {

    private static final Logger logger = LoggerFactory.getLogger(DonationController.class);

    @Autowired
    private DonationService donationService; // Receiver for the commands

    @PostMapping("/api/donations")
    public CompletableFuture<ResponseEntity<DonationResponse>> makeDonation(
            @RequestBody CreateDonationRequest req,
            @AuthenticationPrincipal UserDetailsImpl currentUserDetails) {

        if (currentUserDetails == null) {
            logger.warn("Attempt to make donation by unauthenticated user for campaign ID: {}", req.getCampaignId());
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            );
        }

        UUID userId = currentUserDetails.getId();
        logger.info("Processing donation from user ID: {} for campaign ID: {}", userId, req.getCampaignId());

        // Create and execute MakeDonationCommand
        MakeDonationCommand makeDonationCommand = new MakeDonationCommand(
                donationService,
                userId,
                req.getCampaignId(),
                req.getAmount(),
                req.getMessage()
        );

        return makeDonationCommand.execute() // Execute the command
                .thenApply(donation -> {
                    logger.info("Donation successful for user ID: {}, campaign ID: {}, donation ID: {}", userId, req.getCampaignId(), donation.getId());
                    return ResponseEntity
                            .status(HttpStatus.CREATED)
                            .body(DonationResponse.from(donation));
                })
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    logger.error("Error during donation creation for user ID: {}, campaign ID: {}. Error: {}", userId, req.getCampaignId(), cause.getMessage(), cause);
                    if (cause instanceof IllegalArgumentException) {
                        return ResponseEntity.badRequest().body(null);
                    } else if (cause instanceof RuntimeException && cause.getMessage() != null && cause.getMessage().contains("Saldo tidak mencukupi")) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
                    }
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                });
    }

    @GetMapping("/api/donations/campaign/{campaignId}")
    public ResponseEntity<List<DonationResponse>> getByCampaign(@PathVariable String campaignId) {
        logger.debug("Fetching donations for campaign ID: {}", campaignId);
        List<Donation> list = donationService.findDonationsByCampaign(campaignId);
        List<DonationResponse> resp = list.stream()
                .map(DonationResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/api/donations/{id}/message")
    public CompletableFuture<ResponseEntity<DonationResponse>> removeMessage(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetailsImpl currentUserDetails) {

        if (currentUserDetails == null) {
            logger.warn("Attempt to remove message for donation ID: {} by unauthenticated user", id);
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            );
        }
        UUID requestingUserId = currentUserDetails.getId();
        logger.info("User ID: {} attempting to remove message for donation ID: {}", requestingUserId, id);

        // Create and execute RemoveDonationMessageCommand
        RemoveDonationMessageCommand removeMessageCommand = new RemoveDonationMessageCommand(
                donationService,
                id, // donationId
                requestingUserId
        );

        return removeMessageCommand.execute() // Execute the command
                .thenApply(donation -> {
                    logger.info("Message removed successfully for donation ID: {} by user ID: {}", id, requestingUserId);
                    return ResponseEntity.ok(DonationResponse.from(donation));
                })
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    logger.error("Error removing donation message for donation ID: {} by user ID: {}. Error: {}", id, requestingUserId, cause.getMessage(), cause);
                    if (cause instanceof UnauthorizedException) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
                    } else if (cause instanceof DonationNotFoundException) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                    }
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                });
    }

    @GetMapping("/api/donations/my-history")
    public ResponseEntity<List<DonationResponse>> getMyHistory(
            @AuthenticationPrincipal UserDetailsImpl currentUserDetails) {

        if (currentUserDetails == null) {
            logger.warn("Attempt to fetch donation history by unauthenticated user");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UUID userId = currentUserDetails.getId();
        logger.debug("Fetching donation history for user ID: {}", userId);

        List<Donation> list = donationService.findDonationsByDonor(userId);
        List<DonationResponse> resp = list.stream()
                .map(DonationResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }
}
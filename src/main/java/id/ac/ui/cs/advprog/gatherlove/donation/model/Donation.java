package id.ac.ui.cs.advprog.gatherlove.donation.model;

// Minimal skeleton
public class Donation {
    // Empty or just basic fields without annotations
    private java.util.UUID id;
    public java.util.UUID getId() { return id; }
    // Need builder, other fields, getters/setters later for test assertions
    public static DonationBuilder builder() { return new DonationBuilder(); }
    public static class DonationBuilder {
        public DonationBuilder campaignId(java.util.UUID id){ return this;}
        public DonationBuilder donorId(java.util.UUID id){ return this;}
        public DonationBuilder amount(java.math.BigDecimal a){ return this;}
        public DonationBuilder message(String m){ return this;}
        public Donation build() { return new Donation(); }
    }
}
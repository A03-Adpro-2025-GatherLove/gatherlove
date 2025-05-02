package id.ac.ui.cs.advprog.gatherlove.donation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class DonationDummy {

    @Id
    private Long id;

    private Long amount;
    
    private String name;

    public DonationDummy() {}

    public DonationDummy(Long id, Long amount, String name) {
        this.id = id;
        this.amount = amount;
        this.name = name;
    }
    
}

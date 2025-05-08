package id.ac.ui.cs.advprog.gatherlove.authentication.model;

//import id.ac.ui.cs.advprog.gatherlove.authentication.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 20)
    private String name;
}
package entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;


    @OneToOne
    private Product product;
    private int quantity;
}

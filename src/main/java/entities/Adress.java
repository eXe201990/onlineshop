package entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@Setter
@Getter
public class Adress {

    private String street;
    private String city;
    private String zipcode;
    private Long number;


}

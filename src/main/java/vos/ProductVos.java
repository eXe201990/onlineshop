package vos;

import enums.Currencies;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Enumerated;

import static javax.persistence.EnumType.STRING;

@Data
public class ProductVos {

    private long id;
    private String code;
    private String description;
    private double price;
    private int stock;
    private boolean valid;
    @Enumerated(STRING)
    private Currencies currencies;

}

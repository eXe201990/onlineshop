package repository;

import entities.Product;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static enums.Currencies.USD;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@Data
class ProductRepositoryIntegrationTest {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private TestEntityManager testEntityManager;


    @Test
    public void findByCodeWhenCodeIsPresentInDb_shouldReturnProduct(){
        Product product = new Product();
        product.setCode("AProductCode");
        product.setPrice(100);
        product.setStock(1);
        product.setValid(true);
        product.setCurrencies(USD);
        product.setDescription("ABadPRoduct");


        Product product2 = new Product();
        product2.setCode("AProductCode");
        product2.setPrice(100);
        product2.setStock(1);
        product2.setValid(true);
        product2.setCurrencies(USD);
        product2.setDescription("ABadPRoduct");

        testEntityManager.persist(product2);
        testEntityManager.persist(product);
        testEntityManager.flush();

      Optional<Product> productFromDb =  repository.findById(product.getCode());

      assertThat(productFromDb).isPresent();
      assertThat(productFromDb.get().getCode()).isEqualTo(product.getCode());

    }

    @Test
    public void findByCode_whenCodeIsNotPresentInDbShouldReturnEmpty() {
       Optional<Product> productFromDb = repository.findById("asd");

       assertThat(productFromDb).isNotPresent();
    }

}
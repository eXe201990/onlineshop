package repository;

import entities.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface  ProductRepository  extends CrudRepository<Product,Long> {


    Optional<Product> findById(String code);
}

package services;

import entities.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repository.ProductRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final ProductRepository productRepository;

    public boolean isHavingEnoughStock(Integer productId,Integer quantity) {
         Product productO = productRepository.findById(productId.longValue()).get();
         if(productO.getStock() >= quantity) {
             return  true;
         }
            return false;

    }
}

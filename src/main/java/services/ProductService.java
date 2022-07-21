package services;

import entities.Product;
import exceptios.InvalidProductCodeException;
import lombok.RequiredArgsConstructor;
import mappers.ProductMappers;
import org.springframework.stereotype.Service;
import repository.ProductRepository;
import vos.ProductVos;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMappers productMappers;

    private final ProductRepository productRepository;


    public void addProduct(ProductVos productVo, Long customerId) {
        System.out.println("Customer with id " + customerId + " is in service");
       Product product = productMappers.toEntity(productVo);
       productRepository.save(product);
    }

    public ProductVos getProduct(String productCode) throws InvalidProductCodeException {
        Product product = getProductEntity(productCode);

        ProductVos productVo = productMappers.toVo(product);
        return  productVo;
    }



    public List<ProductVos> getProducts() {
        List<ProductVos> products = new ArrayList<>();
        Iterable<Product> productFromDb = productRepository.findAll();
        Iterator<Product> iterator = productFromDb.iterator();

        while(iterator.hasNext()){
            Product product =  iterator.next();
            ProductVos productVos =productMappers.toVo(product);
            products.add(productVos);
        }
        return products;
       }


    public void updateProduct(ProductVos productVos, Long customerId) throws InvalidProductCodeException {
        System.out.println("Customer with id " +  customerId + " is in service for update");
        if(productVos.getCode() == null) {
            throw new InvalidProductCodeException();
        }

        Product product = getProductEntity(productVos.getCode());
        product.setValid(productVos.isValid());
        product.setPrice(productVos.getPrice());
        product.setDescription(productVos.getDescription());
        product.setStock(productVos.getStock());
        product.setCurrencies(productVos.getCurrencies());

        productRepository.save(product);
    }


    public void deleteProduct(String productCode , Long customerId) throws InvalidProductCodeException {
        System.out.println("User with Id " + customerId + " is deleting " + productCode);
        if(productCode == null) {
            throw  new InvalidProductCodeException();
        }

       Product  productOptional =  getProductEntity(productCode);
        productRepository.delete(productOptional);

    }

    private Product getProductEntity(String productCode) throws InvalidProductCodeException {
        Optional<Product> productOptional = productRepository.findById(productCode);

        if (!productOptional.isPresent()) {
            throw new InvalidProductCodeException();
        }
        return productOptional.get();
    }

    @Transactional
    public void addStock(String productCode,Integer quantity,Long customerId) throws InvalidProductCodeException {
        System.out.println("User with Id " + customerId + " is adding stock for " + productCode + " number of items " + quantity);
        if(productCode == null) {
            throw  new InvalidProductCodeException();
        }

        Product product = getProductEntity(productCode);

        int oldStock = product.getStock();
        product.setStock(oldStock + quantity);
    }

    }


package controller;

import entities.Product;
import exceptios.InvalidProductCodeException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import services.ProductService;
import vos.ProductVos;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    @PostMapping("/{customerId}")
    public void addProduct(@RequestBody ProductVos productVo, @PathVariable Long customerId) {

        productService.addProduct(productVo, customerId);
    }

    @GetMapping("/{productCode}")
    public ProductVos getProduct(@PathVariable String productCode) throws InvalidProductCodeException {
       return  productService.getProduct(productCode);
    }

    @GetMapping
    public ProductVos[] getProducts() {
        return productService.getProducts().toArray(new ProductVos[]{});
    }

    @PutMapping("/{customerId}")
    public void updateProduct(@RequestBody ProductVos productVos , @PathVariable Long customerId) throws InvalidProductCodeException {
        productService.updateProduct(productVos,customerId);

    }

    @DeleteMapping("/{productCode}/{customerId}")
    public void deleteProduct(@PathVariable String productCode ,  @PathVariable Long customerId) throws InvalidProductCodeException {
        productService.deleteProduct(productCode,customerId);
    }

    @PatchMapping("/{productCode}/{quantity}/{customerId}")
    public void addStock(@PathVariable String productCode , @PathVariable Integer quantity, @PathVariable Long customerId) throws InvalidProductCodeException {
        productService.addStock(productCode,quantity,customerId);
    }

}

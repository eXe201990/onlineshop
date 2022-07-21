package services;

import entities.Product;
import exceptios.InvalidProductCodeException;
import mappers.ProductMappers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import repository.ProductRepository;
import vos.ProductVos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static enums.Currencies.EUR;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowableOfType;


import static org.assertj.core.internal.bytebuddy.asm.Advice.OffsetMapping.ForEnterValue.Factory.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
class ProductServiceTest {

    @TestConfiguration
    static class ProductServiceTestContextConfiguration {

        @MockBean
        private ProductMappers productMappers;

        @MockBean
        private ProductRepository productRepository;


        @Bean
        public ProductService productService() {
            return new ProductService(productMappers,productRepository);
        }
    }


    @Autowired
    private ProductService productService;


    @Autowired
    private ProductMappers productMappers;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void getProduct_whenProductIsNotInDbShouldThrowAnException() {
        try {


            productService.getProduct("asd");
        }catch (InvalidProductCodeException e) {
            assert  true;
            return;
        }
            assert false;



    }

    @Test
    public void getProduct_whenProductIsInDBshouldReturnIT() throws InvalidProductCodeException {
        Product product = new Product();
        product.setCode("aCode");
        when(productRepository.findById(any())).thenReturn(of(product));
        ProductVos productVo = new ProductVos();
        productVo.setCode("aCode");

        when(productMappers.toVo(any())).thenReturn(productVo);

       ProductVos productReturn = productService.getProduct("aCode");

       assertThat(productReturn.getCode()).isEqualTo("aCode");

       verify(productRepository).findById("aCode");
       verify(productMappers).toVo(product);
    }


    @Test
    public void getProducts() {
        ArrayList<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setCode("aCode");
        products.add(product1);
        Product products2 = new Product();
        products2.setCode("aCode2");
        products.add(products2);

        ProductVos productVo1 = new ProductVos();
        product1.setCode("aCode");
        ProductVos productVos2 = new ProductVos();
        productVos2.setCode("aCode2");


        when(productRepository.findAll()).thenReturn(products);
        when(productMappers.toVo(product1)).thenReturn(productVo1);
        when(productMappers.toVo(products2)).thenReturn(productVos2);

        List<ProductVos> productList = productService.getProducts();

        assertThat(productList).hasSize(2);
        assertThat(productList).containsOnly(productVo1, productVos2);

        verify(productRepository).findAll();
        verify(productMappers).toVo(product1);
        verify(productMappers).toVo(products2);

    }

    @Test
    public void updateProduct_whenProductCodeIsNullshouldThrownAnException() {
        ProductVos productVeo = new ProductVos();

        InvalidProductCodeException invalidProductCodeException =
                catchThrowableOfType(() -> productService.updateProduct(productVeo, 1L), InvalidProductCodeException.class);

    }

    @Test
    public void addProduct() {

        Product product = new Product();
        product.setCurrencies(EUR);
        product.setPrice(123);
        product.setValid(true);
        product.setStock(10);
        product.setCode("AProductionCode");
        when(productMappers.toEntity(any())).thenReturn(product);

        ProductVos productVo = new ProductVos();
        productVo.setValid(true);
        productVo.setDescription("aDescription");
        productVo.setStock(1);
        productVo.setPrice(11);
        productVo.setCurrencies(EUR);
        productVo.setId(1);
        productVo.setCode("AProductionCOde");

        Long customerId = 99L;
        productService.addProduct(productVo,customerId);

        verify(productMappers).toEntity(productVo);
        verify(productRepository).save(product);
    }

    @Test
    public void updateProduct_whenProductCodeIsValid_shouldUpdateTheProduct() throws InvalidProductCodeException {
        ProductVos productVos = new ProductVos();
        productVos.setCode(" new Code");
        productVos.setDescription("A new Description");

        Product product = new Product();
        product.setCode("aCode");
        product.setDescription("an old Description");

        when(productRepository.findById((Long) any())).thenReturn(of(product));

        productService.updateProduct(productVos, 1L);

        verify(productRepository).findById(productVos.getCode());

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productArgumentCaptor.capture());

        Product productSendCapture = productArgumentCaptor.getValue();

        assertThat(productSendCapture.getDescription()).isEqualTo(productVos.getDescription());
    }

    @Test
    public void deleteProduct_whenCodeIsNullShouldThrowAndException() throws InvalidProductCodeException {
        try {
            productService.deleteProduct(null, 1L);
        }catch (InvalidProductCodeException e) {
            assert true;
            return;
        }
        assert false;
    }

    @Test
    public void deleteProduct_whenCodeIsValidhouldDeleteProduct() throws InvalidProductCodeException {
        Product product = new Product();
        product.setCode("aCode");
        when(productRepository.findById((Long) any())).thenReturn(of(product));
        productService.deleteProduct("aCode", 1L);

        verify(productRepository.findById("aCode"));
        verify(productRepository).delete(product);
    }

}
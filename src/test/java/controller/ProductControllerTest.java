package controller;

import entities.Adress;
import entities.Product;
import entities.User;
import enums.Roles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import repository.ProductRepository;
import repository.UserRepository;
import utils.UtilsComponent;
import vos.ProductVos;

import java.util.Optional;

import static enums.Currencies.EUR;
import static enums.Currencies.RON;
import static enums.Roles.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpStatus.BAD_REQUEST;


@SpringBootTest(webEnvironment = RANDOM_PORT)
class ProductControllerTest {

    @TestConfiguration
    static class ProductConfigurationIntegrationTest {

        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        }
    }

    public static final String LOCALHOST = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private ProductController productController;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RestTemplate restTaRestTemplateForPatch;

    @Autowired
    private UtilsComponent utilsComponent;

    @Test
    public void addProduct_whenUserIsAdmin_shouldStoreAProduct() {
        User userEntity = utilsComponent.saveUserWithRoles(Roles.ADMIN);

        ProductVos productVo = new ProductVos();
        productVo.setCode("aProductCode");
        productVo.setPrice(100);
        productVo.setCurrencies(RON);
        productVo.setStock(12);
        productVo.setDescription("A product description");
        productVo.setValid(true);

        testRestTemplate.postForEntity(LOCALHOST + port + "/product/" + userEntity.getId(),productVo, Void.class);


        Iterable<Product> products = productRepository.findAll();
       // assertThat(products).hasSize(1);


        Product product = products.iterator().next();

        assertThat(product.getCode()).isEqualTo(productVo.getCode());
    }


    @Test
    public void contextLoads() {
       assertThat(productController).isNotNull();
    }


    @Test
    public void addProduct_whenUserIsNotInDbShouldThrowInvalidCustomerException() {

        ProductVos productVo = new ProductVos();
        productVo.setCode("aProductCode");
        productVo.setPrice(100);
        productVo.setCurrencies(RON);
        productVo.setStock(12);
        productVo.setDescription("A product description");
        productVo.setValid(true);

        ResponseEntity<String> response = testRestTemplate.postForEntity(LOCALHOST + port + "/product/123" ,productVo,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Id ul trimis este invalid!");

    }


    @Test
    public void addProduct_whenUserIsNOTAdmin_shouldThrowInvalidOperationException() {
        User userEntity = utilsComponent.saveUserWithRoles(Roles.CLIENT);

        ProductVos productVo = new ProductVos();
        productVo.setCode("aProductCode");
        productVo.setPrice(100);
        productVo.setCurrencies(RON);
        productVo.setStock(12);
        productVo.setDescription("A product description");
        productVo.setValid(true);

       ResponseEntity<String> response =
               testRestTemplate.postForEntity(LOCALHOST + port + "/product/" + userEntity.getId(),productVo, String.class);


       assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
       assertThat(response.getBody()).isEqualTo("Utilizatorul nu are permisiunea de a executa aceasta operatiune.");
        Iterable<Product> products = productRepository.findAll();

    }



    @Test
    public void getProductByCode_whenCodeIsPresentInDB_shouldReturnTheProduct() {

        Product product = utilsComponent.storeTwoProductsInDatabase("ISBN_12313_33", "ISBN8080-0901");

        ProductVos productResponse =
               testRestTemplate.getForObject(LOCALHOST + port + "/product/" + product.getCode(), ProductVos.class);

       assertThat(productResponse.getCode()).isEqualTo(product.getCode());
    }



    @Test
    public void getProductByCode_shouldReturnErrorMessage_whenProductCodeIsNotPresent() {
       String response = testRestTemplate.getForObject(LOCALHOST + port + "/product/123321", String.class);

       assertThat(response).isEqualTo("Codul produsul trimis este invalid!");
    }

    @Test
    public void getProducts() {
        productRepository.deleteAll();
        utilsComponent.storeTwoProductsInDatabase("ISBN_12313_33", "ISBN8080-0901");
         ProductVos[]  products = testRestTemplate.getForObject(LOCALHOST + port , ProductVos[].class);

         assertThat(products).hasSize(2);
         assertThat(products[0].getCode()).contains("ISBN_12313_33");
        assertThat(products[1].getCode()).contains("ISBN8080-0901");
    }


    @Test
    public void addStock_whenAddingStockTotheItemByAdmin_shouldSavedInDb() {
        Product product = utilsComponent.generateProduct("aProductForAddingStock");
        productRepository.save(product);

        User user  = utilsComponent.saveUserWithRoles(ADMIN);

        restTaRestTemplateForPatch.exchange(LOCALHOST + port + "/product/" + product.getCode() + "/3/" + user.getId(),
                PATCH, HttpEntity.EMPTY,Void.class);

        Product productfromDb = productRepository.findById(product.getCode()).get();

        assertThat(productfromDb.getStock()).isEqualTo(4);

    }


    @Test
    public void updateProductWhenUserIsEditorShouldUpdateProduct() {
        Product product = utilsComponent.generateProduct("aProduct");
        productRepository.save(product);

        User user = utilsComponent.saveUserWithRoles(EDITOR);

        ProductVos productVo = new ProductVos();
        productVo.setCode(product.getCode());
        productVo.setPrice(200L);
        productVo.setCurrencies(EUR);
        productVo.setStock(200);
        productVo.setDescription("A product description");
        productVo.setValid(false);

        testRestTemplate.put(LOCALHOST + port + "/product" + user.getRoles(),productVo);

        Optional<Product> updateProduct = productRepository.findById(productVo.getCode());

        assertThat(updateProduct.get().getDescription()).isEqualTo(productVo.getDescription());
        assertThat(updateProduct.get().getCurrencies()).isEqualTo(productVo.getCurrencies());
        assertThat(updateProduct.get().getPrice()).isEqualTo(productVo.getPrice());
        assertThat(updateProduct.get().getStock()).isEqualTo(productVo.getStock());
        assertThat(updateProduct.get().isValid()).isEqualTo(productVo.isValid());
    }


    @Test
    public void updateProductWhenUserIsAdmin_shouldUpdateProduct() {
        Product product = utilsComponent.generateProduct("aProduct100");
        productRepository.save(product);

        User user = utilsComponent.saveUserWithRoles(ADMIN);

        ProductVos productVo = new ProductVos();
        productVo.setCode(product.getCode());
        productVo.setPrice(200L);
        productVo.setCurrencies(EUR);
        productVo.setStock(200);
        productVo.setDescription("A product description");
        productVo.setValid(false);

        testRestTemplate.put(LOCALHOST + port + "/product" + user.getRoles(),productVo);

        Optional<Product> updateProduct = productRepository.findById(productVo.getCode());

        assertThat(updateProduct.get().getDescription()).isEqualTo(productVo.getDescription());
        assertThat(updateProduct.get().getCurrencies()).isEqualTo(productVo.getCurrencies());
        assertThat(updateProduct.get().getPrice()).isEqualTo(productVo.getPrice());
        assertThat(updateProduct.get().getStock()).isEqualTo(productVo.getStock());
        assertThat(updateProduct.get().isValid()).isEqualTo(productVo.isValid());
    }


    @Test
    public void deleteProductwhenUserIsAdmin() {
        Product product = utilsComponent.generateProduct("aProductForDelete");
        productRepository.save(product);

        testRestTemplate.delete(LOCALHOST + port + "/port/" + product.getCode() + "/1" );

        assertThat(productRepository.findById(product.getCode())).isPresent();
    }


    @Test
    public void deleteProductwhenUserIsClient() {
        Product product = utilsComponent.generateProduct("aProductForDelete");
        productRepository.save(product);

        testRestTemplate.delete(LOCALHOST + port + "/port/" + product.getCode() + "/2" );

        assertThat(productRepository.findById(product.getCode())).isPresent();
    }


    @Test
    public void updateProductWhenUserIsCLIENT_shouldNOTUpdateProduct() {
        Product product = utilsComponent.generateProduct("aProduct");
        productRepository.save(product);

        User user = utilsComponent.saveUserWithRoles(CLIENT);

        ProductVos productVo = new ProductVos();
        productVo.setCode(product.getCode());
        productVo.setPrice(200L);
        productVo.setCurrencies(EUR);
        productVo.setStock(200);
        productVo.setDescription("A product description");
        productVo.setValid(false);

        testRestTemplate.put(LOCALHOST + port + "/product" + user.getRoles(),productVo);

        Optional<Product> updateProduct = productRepository.findById(productVo.getCode());

        assertThat(updateProduct.get().getDescription()).isEqualTo(product.getDescription());
        assertThat(updateProduct.get().getCurrencies()).isEqualTo(product.getCurrencies());
        assertThat(updateProduct.get().getPrice()).isEqualTo(product.getPrice());
        assertThat(updateProduct.get().getStock()).isEqualTo(product.getStock());
        assertThat(updateProduct.get().isValid()).isEqualTo(product.isValid());
    }





}
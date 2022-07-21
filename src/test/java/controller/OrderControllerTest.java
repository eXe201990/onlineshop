package controller;
import entities.OrderItem;
import entities.Orders;
import entities.Product;
import entities.User;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import repository.OrderRepository;
import utils.UtilsComponent;
import vos.OrderVO;


import javax.transaction.Transactional;
import java.util.*;

import static enums.Roles.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class OrderControllerTest {

    @TestConfiguration
    static class ProductControllerIntegrationTextContextConfiguration {

        @Bean
        public RestTemplate restTemplateForPatch() {
            return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        }
    }

    public static final String LOCALHOST = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate restTemplateForPatch;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UtilsComponent utilComponent;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @Transactional
    public void addOrderWhenOrderisValid_ShouldAddItToDb() {
        User user = utilComponent.saveUserWithRoles(CLIENT);
        Product product = utilComponent.storeTwoProductsInDatabase("code1", "code2");
        OrderVO orderVo = utilComponent.createOrderVeo(user, product);

        testRestTemplate.postForEntity(LOCALHOST + port + "/order/ ", orderVo, Void.class);

        List<Orders> ordersIterable = (List<Orders>) orderRepository.findAll();
        Optional<OrderItem> orderItemOptional = ordersIterable.stream()
                .map(order -> (List<OrderItem>) order.getOrderItems())
                .flatMap(List::stream)
                .filter(orderItem -> orderItem.getProduct().getId() == product.getId())
                .findFirst();

        assertThat(orderItemOptional.isPresent());

        // order1 =>  orderITems -> 1,2,3
        // order2 => orderItems -> 3,4
        // list (orderItems), list (orderItems2) -> List (orderItems1, orderItems2) => FlatMap

//     Orders order =   ordersIterable.iterator().next();
//
//       assertThat(((List<OrderItem>)order.getOrderItemCollection()).get(0).getProduct().getId()).isEqualTo(product.getId());
    }


    @Test
    public void addOrderWhenRequestIsMadeByAdminShouldThrowAnException() {

        User user = utilComponent.saveUserWithRoles(CLIENT);
        Product product = utilComponent.storeTwoProductsInDatabase("code20Admin", "code19Admin");

        OrderVO orderVo = utilComponent.createOrderVeo(user, product);
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(LOCALHOST + port + "/order/ ", orderVo, String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo("Utilizatorul nu are permisiunea de a executa aceasta operatie ");
    }


    @Test
    public void addOrderWhenRequestIsMadeByExpeditorShoudThrowException() {
        User user = utilComponent.saveUserWithRoles(EXPEDITOR);
        Product product = utilComponent.storeTwoProductsInDatabase("codeForExpeditorAdmin", "code9ForExpeditor");

        OrderVO orderVo = utilComponent.createOrderVeo(user, product);
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(LOCALHOST + port + "/order/ ", orderVo, String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo("Utilizatorul nu are permisiunea de a executa aceasta operatie ");

    }


    @Test
    public void deliver_whenHavingAnOrderWichIsNotCanceledShouldDeliverByExpeditor() {

        User expeditor = utilComponent.saveUserWithRoles(EXPEDITOR);
        User client = utilComponent.saveUserWithRoles(CLIENT);

        Product product = utilComponent.storeTwoProductsInDatabase("codeForExpeditorAdmin", "code9ForExpeditor");


        Collection<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = utilComponent.generateOrderItem(product);
        Orders ordersWithProduct = utilComponent.generateOrderItem(product, client);

        orderRepository.save(ordersWithProduct);

        restTemplateForPatch.exchange(LOCALHOST + port + "/order" + ordersWithProduct.getId() + "/" + expeditor.getId(), PATCH, HttpEntity.EMPTY, Void.class);

        Orders ordersFromDb = orderRepository.findById(ordersWithProduct.getId()).get();

        assertThat(!ordersFromDb.isDeliverd()).isTrue();

    }


    @Test
    public void deliver_whenHavingAnOrderWichIsNotCanceledShoulNotDeliverByAdmin() {
        User adminAsExpeditor = utilComponent.saveUserWithRoles(ADMIN);
        User client = utilComponent.saveUserWithRoles(CLIENT);

        Product product = utilComponent.storeTwoProductsInDatabase("codeForExpeditorWhenDeliverIsADdmin", "code9ForExpeditorForDeliverWhenAdmin");


        Collection<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = utilComponent.generateOrderItem(product);
        Orders ordersWithProduct = utilComponent.generateOrderItem(product, client);


        orderRepository.save(ordersWithProduct);

        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order" + ordersWithProduct.getId() + "/"
                    + adminAsExpeditor.getId(), PATCH, HttpEntity.EMPTY, String.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400: [Utilizatorul nu are permisiunea de a executa aceasta operatie]");
        }

//        ResponseEntity<String> responseEntity =
//
//
//        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
//        assertThat(responseEntity.getBody()).isEqualTo("Utilizatorul nu are permisiunea de a executa aceasta operatie ");

    }


    @Test
    public void deliver_whenHavingAnOrderWichIsNotCanceledShoulNotDeliverByClient() {
        User expeditor = utilComponent.saveUserWithRoles(EXPEDITOR);
        User client = utilComponent.saveUserWithRoles(CLIENT);
        Product product = utilComponent.storeTwoProductsInDatabase("codeForExpeditoForCancelOrder", "code9ForExpeditorForCancelOrder2");
        Orders ordersWithProduct = utilComponent.generateOrderItem(product, client);

        ordersWithProduct.setCanceled(true);
        orderRepository.save(ordersWithProduct);

        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order" + ordersWithProduct.getId() + "/"
                    + expeditor.getId(), PATCH, HttpEntity.EMPTY, String.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400: [Comanda a fost anulata!]");
        }

    }

    @Test
    public void cancelWhenValidOrderShouldCancelItProduct() {

        User client = utilComponent.saveUserWithRoles(CLIENT);
        Product product = utilComponent.storeTwoProductsInDatabase("codeForCanceledOrder1", "code9ForCanceledOrder2");
        Orders ordersWithProduct = utilComponent.generateOrderItem(product, client);
        ordersWithProduct.setDeliverd(true);
        orderRepository.save(ordersWithProduct);


        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/cancel" + ordersWithProduct.getId() + "/"
                    + client.getId(), PATCH, HttpEntity.EMPTY, String.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400: [ Utilizatorul nu are permisiunea de a executa aceasta operatie!]");
        }

        Orders ordersFromDb = orderRepository.findById(ordersWithProduct.getId()).get();
        assertThat(ordersFromDb.isCanceled()).isTrue();
    }

    @Test
    public void cancelWhenValidOrder_IsAlreadySendShoulThrowExceptionProduct() {
        User client = utilComponent.saveUserWithRoles(CLIENT);
        Product product = utilComponent.storeTwoProductsInDatabase("codeForCanceledOrder1", "code9ForCanceledOrder2");
        Orders ordersWithProduct = utilComponent.generateOrderItem(product, client);
        ordersWithProduct.setDeliverd(true);
        orderRepository.save(ordersWithProduct);


        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/cancel" + ordersWithProduct.getId() + "/"
                    + client.getId(), PATCH, HttpEntity.EMPTY, Void.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400: [ Utilizatorul nu are permisiunea de a executa aceasta operatie!]");
        }

        Orders ordersFromDb = orderRepository.findById(ordersWithProduct.getId()).get();

    }

    @Test
    public void cancelWhenUserISAdmin_ShoulThrowExceptionProduct() {
        User admin = utilComponent.saveUserWithRoles(ADMIN);
        Product product = utilComponent.storeTwoProductsInDatabase("codeForCanceledOrderForAdmin", "code9ForCanceledOrderForAdmin");
        Orders ordersWithProduct = utilComponent.generateOrderItem(product, admin);
        orderRepository.save(ordersWithProduct);


        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/cancel" + ordersWithProduct.getId() + "/"
                    + admin.getId(), PATCH, HttpEntity.EMPTY, Void.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400: [ Utilizatorul nu are permisiunea de a executa aceasta operatie!]");
        }


    }


    @Test
    public void cancelWhenUserISExpeditor_ShoulThrowExceptionProduct() {

        User client = utilComponent.saveUserWithRoles(CLIENT);
        Product product = utilComponent.storeTwoProductsInDatabase("codeForCanceledOrder1", "code9ForCanceledOrder2");
        Orders ordersWithProduct = utilComponent.generateOrderItem(product, client);
        ordersWithProduct.setDeliverd(true);
        orderRepository.save(ordersWithProduct);


        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/cancel" + ordersWithProduct.getId() + "/"
                    + client.getId(), PATCH, HttpEntity.EMPTY, Void.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400: [ Utilizatorul nu are permisiunea de a executa aceasta operatie!]");
        }

        Orders ordersFromDb = orderRepository.findById(ordersWithProduct.getId()).get();
    }

    @Test
    public void deliver_whenHavingAnOrderWichIsCancelShouldThrowAnException() {

        User client = utilComponent.saveUserWithRoles(CLIENT);
        Product product = utilComponent.storeTwoProductsInDatabase("codeForCanceledOrder1", "code9ForCanceledOrder2");
        Orders ordersWithProduct = utilComponent.generateOrderItem(product, client);
        ordersWithProduct.setDeliverd(true);
        orderRepository.save(ordersWithProduct);


        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/cancel" + ordersWithProduct.getId() + "/"
                    + client.getId(), PATCH, HttpEntity.EMPTY, Void.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400: [ Utilizatorul nu are permisiunea de a executa aceasta operatie!]");
        }

        Orders ordersFromDb = orderRepository.findById(ordersWithProduct.getId()).get();

    }



    @Test
    @Transactional
    public void return_whenOrderValid_shouldReturnIt() {
        User client = utilComponent.saveUserWithRoles(CLIENT);
        Product product = utilComponent.storeTwoProductsInDatabase("codeForReturn1", "code9ForReturn2");
        Orders ordersWithProduct = utilComponent.saveOrder(client, product);


        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/return" + ordersWithProduct.getId() + "/"
                    +client.getId(), PATCH, HttpEntity.EMPTY, Void.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400: [  Comanda a fost deja Expediata!]");
        }

        Orders orderFromDb = orderRepository.findById(ordersWithProduct.getId()).get();

        assertThat(orderFromDb.isReturned()).isTrue();
        assertThat(orderFromDb.getOrderItems().get(0).getProduct().getStock()).isEqualTo(product.getStock()
                +  ordersWithProduct.getOrderItems().get(0).getQuantity());


    }



    @Test
    public void cancel_whenValidOrderShouldCancelIt() {

        User client = utilComponent.saveUserWithRoles(CLIENT);
        Product product = utilComponent.storeTwoProductsInDatabase("codeForCancel1", "code9ForCancel2");
        Orders ordersWithProduct = utilComponent.generateOrderItem(product, client);
        ordersWithProduct.setDeliverd(true);
        orderRepository.save(ordersWithProduct);

        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/cancel/" + ordersWithProduct.getId() + "/"
                    + client.getId(), PATCH, HttpEntity.EMPTY, Void.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400: [  Utilizatorul nu are permisiunea de a executa aceasta operatie !]");


            Orders orderFromDb = orderRepository.findById(ordersWithProduct.getId()).get();
            assertThat(orderFromDb.isCanceled()).isTrue();

        }
    }

    @Test
    public void cancel_WhenUserIsAdmin_ShouldThrowAnException() {

        User admin = utilComponent.saveUserWithRoles(ADMIN);
        Product product = utilComponent.storeTwoProductsInDatabase("codeForCancelOrSendOrder", "code9ForCancelOrSendOrder2");
        Orders ordersWithProduct = utilComponent.generateOrderItem(product, admin);
        ordersWithProduct.setDeliverd(true);
        orderRepository.save(ordersWithProduct);

        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/cancel/" + ordersWithProduct.getId() + "/"
                    + admin.getId(), PATCH, HttpEntity.EMPTY, Void.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400: [  Comanda a fost deja Expediata!]");


            Orders orderFromDb = orderRepository.findById(ordersWithProduct.getId()).get();
            assertThat(orderFromDb.isCanceled()).isTrue();
        }
    }



    @Test
    public void return_whenUserIsAdmin_shouldReturnItShowdTrowException() {
        User admin = utilComponent.saveUserWithRoles(ADMIN);
        Product product = utilComponent.storeTwoProductsInDatabase("codeForCancelOrSendOrder", "code9ForCancelOrSendOrder2");
        Orders ordersWithProduct = utilComponent.generateOrderItem(product, admin);
        ordersWithProduct.setDeliverd(true);
        orderRepository.save(ordersWithProduct);

        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/cancel/" + ordersWithProduct.getId() + "/"
                    + admin.getId(), PATCH, HttpEntity.EMPTY, Void.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400: [  Comanda a fost deja Expediata!]");


            Orders orderFromDb = orderRepository.findById(ordersWithProduct.getId()).get();
            assertThat(orderFromDb.isCanceled()).isTrue();
        }
    }



    @Test
    public void return_whenUserIsExpeditor_shouldReturnItShowdTrowException() {
        User expeditor = utilComponent.saveUserWithRoles(EXPEDITOR);
        Product product = utilComponent.storeTwoProductsInDatabase("codeForCancelAdmin1", "code9ForCancelAdmin2");
        Orders ordersWithProduct =utilComponent.saveCancelOrder(expeditor,product);
        ordersWithProduct.setDeliverd(true);
        orderRepository.save(ordersWithProduct);

        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/cancel/" + ordersWithProduct.getId() + "/"
                    + expeditor.getId(), PATCH, HttpEntity.EMPTY, Void.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400: [  Comanda a fost deja Expediata!]");


            Orders orderFromDb = orderRepository.findById(ordersWithProduct.getId()).get();
            assertThat(orderFromDb.isCanceled()).isTrue();
        }
    }



}



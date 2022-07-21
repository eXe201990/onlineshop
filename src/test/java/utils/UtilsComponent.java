package utils;

import entities.*;
import enums.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import repository.OrderRepository;
import repository.ProductRepository;
import repository.UserRepository;
import vos.OrderVO;

import javax.transaction.Transactional;
import java.util.*;

import static enums.Currencies.RON;

@Component
@RequiredArgsConstructor
public class UtilsComponent {

        private final ProductRepository productRepository;

        private final UserRepository userRepository;

        private  final OrderRepository orderRepository;

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public User saveUserWithRoles(Roles role) {
            User userEntity = new User();
            userEntity.setFirstname("adminFirstName");
            Collection<Roles> roles = new ArrayList<>();
            roles.add(role);
            userEntity.setRoles(roles);
            Adress adress = new Adress();
            adress.setCity("Bucuresti");
            adress.setZipcode("123123");
            adress.setNumber(21L);
            adress.setStreet("Aurel Vlaicu");
            userEntity.setAdress(adress);
            userRepository.save(userEntity);
            return userEntity;
        }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Product storeTwoProductsInDatabase(String code1, String code2) {
        Product product = generateProduct(code1);
        Product product2 = generateProduct(code2);

        product.setCode("adragutCode");
        product.setPrice(100L);
        product.setCurrencies(RON);
        product.setStock(12);
        product.setDescription("A product description");
        product.setValid(true);

        ArrayList<Product> products = new ArrayList<>();
        products.add(product);
        products.add(product2);
        productRepository.saveAll(products);
        return product;

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public  Product generateProduct(String productCode) {
        Product product = new Product();
        product.setCode(productCode);
        product.setPrice(100L);
        product.setCurrencies(RON);
        product.setStock(12);
        product.setDescription("A product description");
        product.setValid(true);
        return product;
    }

    public Orders generateOrderItem(Product product, User user) {
        Orders order = new Orders();
        order.setUser(user);
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = new OrderItem();
        orderItems.add(orderItem);
        order.setOrderItems(orderItems);
        return order;

    }


    public OrderItem generateOrderItem(Product product) {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(1);
        orderItem.setProduct(product);
        return orderItem;
    }

    public OrderVO createOrderVeo(User user, Product product) {
        OrderVO orderVo = new OrderVO();
        orderVo.setUserId((int)(user.getId()));
        Map<Integer, Integer> orderMap = new HashMap<>();
        orderMap.put((int) product.getId(), 1);
        orderVo.setProductsIdsToQuantity(orderMap);
        return orderVo;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Orders saveCancelOrder(User client, Product product) {
        Orders ordersWithProduct = generateOrderItem(product, client);
        ordersWithProduct.setDeliverd(true);
        orderRepository.save(ordersWithProduct);
        return ordersWithProduct;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Orders saveOrder(User client, Product product) {
        Orders ordersWithProduct = generateOrderItem(product, client);
        ordersWithProduct.setDeliverd(true);
        orderRepository.save(ordersWithProduct);
        return ordersWithProduct;
    }



}


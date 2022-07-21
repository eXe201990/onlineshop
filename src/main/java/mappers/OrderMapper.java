package mappers;

import entities.OrderItem;
import entities.Orders;
import entities.Product;
import entities.User;
import exceptios.InvalidCustomerIdException;
import exceptios.InvalidOperationException;
import exceptios.InvalidProductIdException;
import exceptios.InvalidProductsException;
import lombok.RequiredArgsConstructor;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Component;
import repository.ProductRepository;
import repository.UserRepository;
import vos.OrderVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private UserRepository userRepository;

    private final ProductRepository productRepository;

    public Orders toEntity(OrderVO orderVO) throws InvalidCustomerIdException, InvalidProductsException, InvalidProductIdException {
        if(orderVO == null ){
            return null;
        }

        validateOrder(orderVO);

        Orders order = new Orders();

        Optional<User> userOptional = userRepository.findById(orderVO.getUserId().longValue());

        if(!userOptional.isPresent()) {
            throw new InvalidCustomerIdException();
        }

        order.setUser(userOptional.get());


        Map<Integer, Integer> productsIdsToQuantityMap = orderVO.getProductsIdsToQuantity();
        List<OrderItem> orderItemList = new ArrayList<>();
        for(Integer productId : productsIdsToQuantityMap.keySet()) {
            OrderItem  orderItem  = new OrderItem();

            Optional<Product> productOptional = productRepository.findById(productId.longValue());
            if(!productOptional.isPresent()) {
                throw  new InvalidProductIdException();
            }

            orderItem.setProduct(productOptional.get());
            Integer productQuantity = productsIdsToQuantityMap.get(productId);
            orderItem.setQuantity(productQuantity);
            orderItemList.add(orderItem);
        }
     order.setOrderItems(orderItemList);
        return order;
    }

    private void validateOrder(OrderVO orderVO) throws InvalidCustomerIdException, InvalidProductsException {

        if(orderVO.getProductsIdsToQuantity().keySet().isEmpty()) {
            throw  new InvalidProductsException();
        }
    }
}

package services;

import entities.Orders;
import entities.Product;
import exceptios.*;
import lombok.RequiredArgsConstructor;
import mappers.OrderMapper;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import repository.OrderRepository;
import vos.OrderVO;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final StockService stockService;

    @PostMapping
    public void  addOrder( @RequestBody  OrderVO orderVO) throws InvalidCustomerIdException, InvalidProductsException, InvalidProductIdException, NotEnoughStockException {
        validateStock(orderVO);
        Orders orders = orderMapper.toEntity(orderVO);

        orders.getOrderItems().forEach(orderItem -> {
            int productStock = orderItem.getProduct().getStock();
            int productIds = (int) orderItem.getProduct().getId();
            int sellstock = orderVO.getProductsIdsToQuantity().get(productIds);
            orderItem.getProduct().setStock(productStock - sellstock);
        });

        orderRepository.save(orders);

    }

    @Transactional
    public void deliver(Integer orderId,Long customerId) throws InvalidOrderIdException, OrderCancelException {
        System.out.println("customer ul cu id ul " + customerId + "este in service");

        thrownExceptionIfOrderIsAbsent(orderId);
        Orders orders = getOrderOrThrowException(orderId);

        if(orders.isCanceled()) {
            throw new OrderCancelException();
        }
        orders.setDeliverd(true);

    }

    private void thrownExceptionIfOrderIsAbsent(Integer orderId) throws InvalidOrderIdException {
        if(orderId == null) {
            throw  new InvalidOrderIdException();
        }
    }

    private Orders getOrderOrThrowException(Integer orderId) throws InvalidOrderIdException {

        Optional<Orders> orderOptional = orderRepository.findById(orderId.longValue());

        if (!orderOptional.isPresent()) {
            throw new InvalidOrderIdException();
        }
        return orderOptional.get();
    }

    @Transactional
    public void cancelOrder(Integer orderId, Long customerId) throws InvalidOrderIdException, OrderAlreadyDeliverdException {
        System.out.println("Customerul cu id " + customerId + " este in service pentru a anula comanda " + orderId);

         thrownExceptionIfOrderIsAbsent(orderId);
         Orders orders = getOrderOrThrowException(orderId);
         if(orders.isDeliverd()){
             throw  new OrderAlreadyDeliverdException();
         }
         orders.setCanceled(true);


    }

    @Transactional
    public void returnOrder(Integer orderId, Long customerId) throws InvalidOrderIdException, OrderAlreadyDeliverdException, OrderNotDeliveredYetException, OrderCancelException {
        System.out.println("Customerul cu id " + customerId + " este in service pentru a returna comanda  cu Id-ul: " + orderId);

        thrownExceptionIfOrderIsAbsent(orderId);
        Orders orders = getOrderOrThrowException(orderId);
        if (!orders.isDeliverd()) {
            throw new OrderNotDeliveredYetException();
        }

        if(orders.isCanceled()){
            throw new OrderCancelException();
        }

        orders.setReturned(true);

        orders.getOrderItems().forEach( orderItem -> {
            Product product = orderItem.getProduct();
            int oldstock = product.getStock();
            product.setStock(oldstock + orderItem.getQuantity());
        });
    }




        private void validateStock(OrderVO orderVO) throws NotEnoughStockException {
        Map<Integer ,Integer> productIdToQuantityMap = orderVO.getProductsIdsToQuantity();
        Set<Integer> productIds = productIdToQuantityMap.keySet();
        for (Integer productId : productIds ) {
            Integer quantity  = productIdToQuantityMap.get(productId);
              boolean havingEnoughStock =   stockService.isHavingEnoughStock(productId,quantity);

              if(!havingEnoughStock) {
                  throw new NotEnoughStockException();
              }
        }
    }
}

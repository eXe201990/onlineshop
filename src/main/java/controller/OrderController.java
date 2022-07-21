package controller;

import exceptios.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import services.OrderService;
import vos.OrderVO;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;


    public void addOrder(OrderVO orderVo) throws InvalidCustomerIdException, InvalidProductsException, InvalidProductIdException, NotEnoughStockException {
        orderService.addOrder(orderVo);
    }


    @PatchMapping("/{orderId}/{customerId}")
    public void deliver(@PathVariable Integer orderId, @PathVariable Long customerId) throws InvalidOrderIdException, OrderCancelException {
        orderService.deliver(orderId, customerId);
    }

    @PatchMapping("/cancel/{orderId}/{customerId}")
    public void cancelOrder(@PathVariable Integer orderId, @PathVariable Long customerId) throws OrderAlreadyDeliverdException, InvalidOrderIdException {
            orderService.cancelOrder(orderId,customerId);
    }

    @PatchMapping("/return/{orderId}/{customerId}")
    public void returnOrder(@PathVariable Integer orderId, @PathVariable Long customerId) throws OrderCancelException, OrderNotDeliveredYetException, OrderAlreadyDeliverdException, InvalidOrderIdException {
        orderService.returnOrder(orderId,customerId);
    }
}

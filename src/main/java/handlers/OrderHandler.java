package handlers;

import exceptios.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.ResponseEntity.status;

@ControllerAdvice
public class OrderHandler {


    @ExceptionHandler(InvalidProductsException.class)
    public ResponseEntity<String> handleInvalidProductsException() {
        return status(BAD_REQUEST).body("Comanda dvs nu contine niciun produs!");
    }

    @ExceptionHandler(InvalidCustomerIdException.class)
    public ResponseEntity<String> handleInvalidCustomerException() {
        return status(BAD_REQUEST).body("CODUL produsului trimis este invalid!");
    }

    @ExceptionHandler(InvalidProductIdException.class)
    public ResponseEntity<String> handleResponseProductCodeException() {
        return status(BAD_REQUEST).body("Id ul produsul nu este valid ");
    }

    @ExceptionHandler(NotEnoughStockException.class)
    public ResponseEntity<String> handleNotEnoughStockException() {
        return status(BAD_REQUEST).body(" Un produs nu a avut stock ul necesar. ");
    }

    @ExceptionHandler(InvalidOrderIdException.class)
    public ResponseEntity<String> handleInvalidOrderExceptionException() {
        return status(BAD_REQUEST).body(" Id ul comenzii nu este valid! ");
    }

    @ExceptionHandler(OrderAlreadyDeliverdException.class)
    public ResponseEntity<String> handleOrderAlreadyDeliveredException() {
        return status(BAD_REQUEST).body(" Comanda a fost deja expediata! ");
    }

    @ExceptionHandler(OrderCancelException.class)
    public ResponseEntity<String> handleOrderException() {
        return status(BAD_REQUEST).body(" Comanda a fost anulata! ");
    }


    @ExceptionHandler(OrderNotDeliveredYetException.class)
    public ResponseEntity<String> handleNotDeliveredYetException() {
        return status(BAD_REQUEST).body(" Comanda nu poate fii returnata deoarce nu a fost livrata! ");
    }

}

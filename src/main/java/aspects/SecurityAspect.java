package aspects;

import entities.User;
import enums.Roles;
import exceptios.InvalidCustomerIdException;
import exceptios.InvalidOperationException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import repository.UserRepository;
import vos.OrderVO;

import java.util.Collection;
import java.util.Optional;

import static enums.Roles.*;

@Aspect
@Component
@RequiredArgsConstructor
public class SecurityAspect {
    private final UserRepository userRepository;

    @Pointcut("execution( * services.ProductService.addProduct(..))")
    public void addProduct() {
    }


    @Pointcut("execution( * services.ProductService.updateProduct(..))")
    public void updateProduct() {
    }

    @Pointcut("execution( * services.ProductService.deleteProduct(..))")
    public void deleteProduct() {
    }

    @Pointcut("execution( * services.OrderService.addOrder(..))")
    public void addOrderPointCut() {
    }

    @Pointcut("execution( * services.OrderService.deliver(..))")
    public void DeliverPointCut() {
    }

    @Pointcut("execution( * services.OrderService.cancelOrder(..))")
    public void cancelOrderPointCut() {
    }

    @Pointcut("execution( * services.OrderService.returnOrder(..))")
    public void returnlOrderPointCut() {
    }

    @Pointcut("execution( * services.OrderService.addStock(..))")
    public void addStockPointCut() {
    }

        @Before("aspects.SecurityAspect.addProduct()")
                public void checkSecurityBeforeAddingProduct(JoinPoint joinPoint) throws InvalidCustomerIdException, InvalidOperationException {
                 Long customerId = (Long) joinPoint.getArgs()[1];
                Optional<User> userOptional = userRepository.findById(customerId);

                if(!userOptional.isPresent()) {
                        throw  new InvalidCustomerIdException();
                }
              User user = userOptional.get();

                if(userIsNotAllowedToAddProduct(user.getRoles())) {
                        throw new InvalidOperationException();
                }

            System.out.println(customerId);
        }

    @Before("aspects.SecurityAspect.addStock()")
    public void checkSecurityBeforeAddingStock(JoinPoint joinPoint) throws InvalidCustomerIdException, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);

        if(!userOptional.isPresent()) {
            throw  new InvalidCustomerIdException();
        }
        User user = userOptional.get();

        if(userIsNotAllowedToAddStock(user.getRoles())) {
            throw new InvalidOperationException();
        }

        System.out.println(customerId);
    }

    private boolean userIsNotAllowedToAddStock(Collection<Roles> roles) {
        return !roles.contains(ADMIN);
    }


    @Before("aspects.SecurityAspect.addOrderPointCut()")
    public void checkSecurityBeforeAddingOrder(JoinPoint joinPoint) throws InvalidCustomerIdException, InvalidOperationException {
      OrderVO orderVo  = (OrderVO) joinPoint.getArgs()[0];
        if(orderVo.getUserId() == null ){
            throw  new InvalidCustomerIdException();
        }

        Optional<User> userOptional = userRepository.findById(orderVo.getUserId().longValue());


        if(!userOptional.isPresent()) {
            throw  new InvalidCustomerIdException();
        }
        User user = userOptional.get();

        if(userIsAllowedToAddAnOrder(user.getRoles())) {
            throw new InvalidOperationException();
        }

        System.out.println(orderVo);
    }




    @Before("aspects.SecurityAspect.updateProduct()")
    public void checkSecurityBeforeUpdatingProduct(JoinPoint joinPoint) throws InvalidCustomerIdException, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);

        if(!userOptional.isPresent()) {
            throw  new InvalidCustomerIdException();
        }
        User user = userOptional.get();

        if(!userIsNotAllowedToUpdateProduct(user.getRoles())) {
            throw new InvalidOperationException();
        }

        System.out.println(customerId);
    }

    @Before("aspects.SecurityAspect.returnOrderPointcut()")
    public void checkSecurityBeforeReturningOrder(JoinPoint joinPoint) throws InvalidCustomerIdException, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);

        if(!userOptional.isPresent()) {
            throw  new InvalidCustomerIdException();
        }
        User user = userOptional.get();

        if(!userIsNotAllowedToReturnOrder(user.getRoles())) {
            throw new InvalidOperationException();
        }

        System.out.println(customerId);
    }



    @Before("aspects.SecurityAspect.cancelOrderPointCut()")
    public void checkSecurityBeforeCancelingOrder(JoinPoint joinPoint) throws InvalidCustomerIdException, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);

        if(!userOptional.isPresent()) {
            throw  new InvalidCustomerIdException();
        }
        User user = userOptional.get();

        if(userIsNotAllowedToCancel(user.getRoles())) {
            throw new InvalidOperationException();
        }

        System.out.println(customerId);
    }


    @Before("aspects.SecurityAspect.deleteProduct()")
    public void checkSecurityBeforeDeleteProduct(JoinPoint joinPoint) throws InvalidCustomerIdException, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);

        if(!userOptional.isPresent()) {
            throw  new InvalidCustomerIdException();
        }
        User user = userOptional.get();

        if(userIsNotAllowedToDeleteProduct(user.getRoles())) {
            throw new InvalidOperationException();
        }

        System.out.println(customerId);
    }


    @Before("aspects.SecurityAspect.deliverPointCut()")
    public void checkSecurityBeforeDeliver(JoinPoint joinPoint) throws InvalidCustomerIdException, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);

        if(!userOptional.isPresent()) {
            throw  new InvalidCustomerIdException();
        }
        User user = userOptional.get();

        if(userIsNotAllowedToDeliver(user.getRoles())) {
            throw new InvalidOperationException();
        }

        System.out.println(customerId);
    }


    private boolean userIsNotAllowedToCancel(Collection<Roles> roles) {
        return !roles.contains(CLIENT);
    }


    private boolean userIsNotAllowedToDeliver(Collection<Roles> roles) {
        return !roles.contains(EXPEDITOR);
    }

    private boolean userIsNotAllowedToAddProduct(Collection<Roles> roles) {
        return !roles.contains(Roles.ADMIN);
    }

    private boolean userIsNotAllowedToDeleteProduct(Collection<Roles> roles) {
        return !roles.contains(Roles.ADMIN);
    }

    private boolean userIsNotAllowedToUpdateProduct(Collection<Roles> roles) {
        return !roles.contains(Roles.ADMIN) && !roles.contains(Roles.EDITOR);
    }

    private boolean userIsNotAllowedToReturnOrder(Collection<Roles> roles) {
        return !roles.contains(CLIENT);
    }

    private boolean userIsAllowedToAddAnOrder(Collection<Roles> roles) {
        return roles.contains(CLIENT);
    }

}



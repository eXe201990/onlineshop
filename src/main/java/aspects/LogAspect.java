package aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Aspect
@Component
public class LogAspect {

    @Pointcut("execution(* controller.ProductController.addProduct(..))")
    public void addProductPointCut() {

    }

    @Pointcut("execution(* controller.OrderController.addOrder(..))")
    public void addOrderPointCut() {

    }

    @Pointcut("execution(* controller.ProductController.updateProduct(..))")
    public void updateProductPointCut() {

    }

    @Pointcut("execution(* controller.OrderController.deliver(..))")
    public void deliverOrderProductPointCut() {

    }

    @Pointcut("execution(* controller.OrderController.cancelOrder(..))")
    public void cancelOrderProductPointCut() {

    }

    @Pointcut("execution(* controller.OrderController.returnOrder(..))")
    public void returnOrderProductPointCut() {

    }

    @Pointcut("execution(* controller.OrderController.addStock(..))")
    public void addStockPointCut() {

    }

    @Before("aspects.LogAspect.updateProductPointCut()")
    public void beforeUpdate(JoinPoint joinPoint){
        System.out.println("In around aspect" +   new Date() + "for doing an update");
        System.out.println("Arguments " + joinPoint.getArgs()[0]);
        System.out.println("The customer had id " + joinPoint.getArgs()[1]);
    }

    @Before("aspects.LogAspect.returnOrderPointCut()")
    public void beforeReturningOrder(JoinPoint joinPoint){
        System.out.println("In around aspect" +   new Date() + "for doing an return");
        System.out.println("Order Id " + joinPoint.getArgs()[0]);
        System.out.println("The customer had id " + joinPoint.getArgs()[1]);
    }


    @Before("aspects.LogAspect.addStockPointCut()")
    public void beforeAddingStock(JoinPoint joinPoint){
        System.out.println("In around aspect" +   new Date() + "for doing an return");
        System.out.println("ProductCOde " + joinPoint.getArgs()[0]);
        System.out.println("Quantity " + joinPoint.getArgs()[1]);
        System.out.println("The customer had id " + joinPoint.getArgs()[2]);
    }



    @Before("aspects.LogAspect.deliverOrderProductPointCut()")
    public void beforeDeliver(JoinPoint joinPoint){
        System.out.println("In around aspect" +   new Date() + "for doing an deliver");
        System.out.println("Arguments " + joinPoint.getArgs()[0]);
        System.out.println("The customer had id " + joinPoint.getArgs()[1]);
    }


    @Before("aspects.LogAspect.cancelOrderPointCut()")
    public void beforeCancel(JoinPoint joinPoint){
        System.out.println("In around aspect" +   new Date() + "for doing an cancelation");
        System.out.println("OrderId " + joinPoint.getArgs()[0]);
        System.out.println("The customeid " + joinPoint.getArgs()[1]);
    }

    @Before("aspects.LogAspect.updateProductPointCut()")
    public void beforeAddingAnOrder(JoinPoint joinPoint){
        System.out.println("In around aspect" +   new Date() + "for doing an update");
        System.out.println("OrderVo " + joinPoint.getArgs()[0]);

    }


    @Before("aspects.LogAspect.addProductPointCut()")
    public void before(JoinPoint joinPoint){
        System.out.println("In around aspect" +   new Date());
        System.out.println("Arguments " + joinPoint.getArgs()[0]);
        System.out.println("The customer had id " + joinPoint.getArgs()[1]);
    }


    @After("aspects.LogAspect.addProductPointCut()")
    public void after(JoinPoint joinPoint){
        System.out.println("In around aspect at " + new Date() );
    }



}

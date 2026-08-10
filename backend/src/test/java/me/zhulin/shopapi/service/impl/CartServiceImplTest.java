package me.zhulin.shopapi.service.impl;

import me.zhulin.shopapi.entity.Cart;
import me.zhulin.shopapi.entity.ProductInOrder;
import me.zhulin.shopapi.entity.User;
import me.zhulin.shopapi.exception.MyException;
import me.zhulin.shopapi.repository.CartRepository;
import me.zhulin.shopapi.repository.OrderRepository;
import me.zhulin.shopapi.repository.ProductInOrderRepository;
import me.zhulin.shopapi.service.ProductService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
public class CartServiceImplTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private ProductService productService;

    @Mock
    private ProductInOrderRepository productInOrderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderRepository orderRepository;

    private User user;

    private ProductInOrder productInOrder;

    private Set<ProductInOrder> set;

    private Cart cart;

    @Before
    public void setUp() {
        user = new User();
        cart = new Cart();

        user.setEmail("email@email.com");
        user.setName("Name");
        user.setPhone("Phone Test");
        user.setAddress("Address Test");

        productInOrder = new ProductInOrder();
        productInOrder.setProductId("1");
        productInOrder.setCount(10);
        productInOrder.setProductPrice(BigDecimal.valueOf(1));

        set = new HashSet<>();
        set.add(productInOrder);

        cart.setProducts(set);

        user.setCart(cart);
    }

    @Test
    public void mergeLocalCartTest() {
        cartService.mergeLocalCart(set, user);

        Mockito.verify(cartRepository, Mockito.times(1)).save(cart);
        Mockito.verify(productInOrderRepository, Mockito.times(1)).save(productInOrder);
    }

    @Test
    public void mergeLocalCartTwoProductTest() {
        ProductInOrder productInOrder2 = new ProductInOrder();
        productInOrder2.setProductId("2");
        productInOrder2.setCount(10);

        user.getCart().getProducts().add(productInOrder2);

        cartService.mergeLocalCart(set, user);

        Mockito.verify(cartRepository, Mockito.times(1)).save(cart);
        Mockito.verify(productInOrderRepository, Mockito.times(1)).save(productInOrder);
        Mockito.verify(productInOrderRepository, Mockito.times(1)).save(productInOrder2);
    }

    @Test
    public void mergeLocalCartNoProductTest() {
        user.getCart().setProducts(new HashSet<>());

        cartService.mergeLocalCart(set, user);

        Mockito.verify(cartRepository, Mockito.times(1)).save(cart);
        Mockito.verify(productInOrderRepository, Mockito.times(1)).save(productInOrder);
    }

    @Test
    public void deleteTest() {
        cartService.delete("1", user);

        Mockito.verify(productInOrderRepository, Mockito.times(1)).deleteById(productInOrder.getId());
    }

    @Test(expected = MyException.class)
    public void deleteNoProductTest() {
        cartService.delete("", user);
    }

    @Test(expected = MyException.class)
    public void deleteNoUserTest() {
        cartService.delete("1", null);
    }

    @Test
    public void checkoutTest() {
        cartService.checkout(user);

        Mockito.verify(productInOrderRepository, Mockito.times(1)).save(productInOrder);
        Mockito.verify(productService, Mockito.times(1)).decreaseStock("1", 10);
        Mockito.verify(orderRepository, Mockito.times(1)).save(Mockito.any());
    }

    // ===== Bo sung theo yeu cau ticket: xac nhan bug that cua mergeLocalCart() =====

    @Test
    public void mergeLocalCartPropagatesExceptionTest() {
        Mockito.when(productInOrderRepository.save(productInOrder))
                .thenThrow(new RuntimeException("DB error"));

        try {
            cartService.mergeLocalCart(set, user);
            Assert.fail("Ky vong RuntimeException duoc nem ra ngoai, nhung khong co exception nao xay ra");
        } catch (RuntimeException e) {
            Assert.assertEquals("DB error", e.getMessage());
        }
    }

    @Test(expected = NullPointerException.class)
    public void deleteNullItemIdThrowsNPEInsteadOfMyExceptionTest() {
        cartService.delete(null, user);
    }

    // Bo sung: getCart() truoc do chua duoc test lan nao (0% coverage)
    @Test
    public void getCartTest() {
        Cart result = cartService.getCart(user);

        Assert.assertEquals(cart, result);
    }
}
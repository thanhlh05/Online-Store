package me.zhulin.shopapi.api;

import me.zhulin.shopapi.entity.Cart;
import me.zhulin.shopapi.entity.User;
import me.zhulin.shopapi.repository.ProductInOrderRepository;
import me.zhulin.shopapi.service.CartService;
import me.zhulin.shopapi.service.ProductInOrderService;
import me.zhulin.shopapi.service.ProductService;
import me.zhulin.shopapi.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @Mock
    private ProductInOrderService productInOrderService;

    @Mock
    private ProductInOrderRepository productInOrderRepository;

    @InjectMocks
    private CartController cartController;

    @Test
    public void mergeCart_WhenServiceThrowsException_ShouldReturnBadRequest() {

        // Arrange
        Principal principal = mock(Principal.class);
        User user = new User();

        when(principal.getName()).thenReturn("test@gmail.com");
        when(userService.findOne("test@gmail.com")).thenReturn(user);

        Collection productInOrders = new HashSet<>();

        // Giả lập CartService bị lỗi
        doThrow(new RuntimeException("Merge cart failed"))
                .when(cartService)
                .mergeLocalCart(any(Collection.class), eq(user));

        // Act
        ResponseEntity response =
                cartController.mergeCart(productInOrders, principal);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
    }
}
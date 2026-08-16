package me.zhulin.shopapi.service.impl;

import me.zhulin.shopapi.entity.Cart;
import me.zhulin.shopapi.entity.ProductInOrder;
import me.zhulin.shopapi.entity.User;
import me.zhulin.shopapi.repository.ProductInOrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
public class ProductInOrderServiceImplTest {

    @Mock
    private ProductInOrderRepository productInOrderRepository;

    @InjectMocks
    private ProductInOrderServiceImpl productInOrderService;

    private User user;
    private Cart cart;
    private ProductInOrder productInOrder;

    @Before
    public void setUp() {
        user = new User();

        cart = new Cart();

        productInOrder = new ProductInOrder();
        productInOrder.setProductId("1");
        productInOrder.setCount(5);

        Set<ProductInOrder> products = new HashSet<>();
        products.add(productInOrder);

        cart.setProducts(products);
        user.setCart(cart);
    }

    // 1. Update sản phẩm thành công
    @Test
    public void updateSuccessTest() {
        productInOrderService.update("1", 10, user);

        assertEquals(Integer.valueOf(10), productInOrder.getCount());

        Mockito.verify(productInOrderRepository)
                .save(productInOrder);
    }

    // 2. Update item không tồn tại
    @Test
    public void updateItemNotFoundTest() {
        productInOrderService.update("999", 10, user);

        Mockito.verify(productInOrderRepository, Mockito.never())
                .save(Mockito.any(ProductInOrder.class));

        // Item cũ không bị thay đổi
        assertEquals(Integer.valueOf(5), productInOrder.getCount());
    }

    // 3. Find item thành công
    @Test
    public void findOneSuccessTest() {
        ProductInOrder result =
                productInOrderService.findOne("1", user);

        assertEquals(productInOrder, result);
        assertEquals("1", result.getProductId());
    }

    // 4. Find item không tồn tại
    @Test
    public void findOneItemNotFoundTest() {
        ProductInOrder result =
                productInOrderService.findOne("999", user);

        assertNull(result);
    }

    // 5. Update số lượng về 0
    // Service hiện tại KHÔNG tự validate quantity.
    // Validation @Min(1) nằm ở tầng API/Form.
    @Test
    public void updateQuantityZeroTest() {
        productInOrderService.update("1", 0, user);

        assertEquals(Integer.valueOf(0), productInOrder.getCount());

        Mockito.verify(productInOrderRepository)
                .save(productInOrder);
    }

    // 6. Update số lượng lớn
    @Test
    public void updateLargeQuantityTest() {
        productInOrderService.update("1", 999, user);

        assertEquals(Integer.valueOf(999), productInOrder.getCount());

        Mockito.verify(productInOrderRepository)
                .save(productInOrder);
    }

    // 7. Find item với itemId null
    @Test(expected = NullPointerException.class)
    public void findOneNullItemIdTest() {
        productInOrderService.findOne(null, user);
    }

}

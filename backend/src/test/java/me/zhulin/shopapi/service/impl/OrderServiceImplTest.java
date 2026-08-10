
package me.zhulin.shopapi.service.impl;

import me.zhulin.shopapi.entity.OrderMain;
import me.zhulin.shopapi.entity.ProductInOrder;
import me.zhulin.shopapi.entity.ProductInfo;
import me.zhulin.shopapi.enums.OrderStatusEnum;
import me.zhulin.shopapi.exception.MyException;
import me.zhulin.shopapi.repository.OrderRepository;
import me.zhulin.shopapi.repository.ProductInfoRepository;
import me.zhulin.shopapi.service.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductInfoRepository productInfoRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderMain orderMain;

    private ProductInfo productInfo;

    @Before
    public void setUp() {
        orderMain = new OrderMain();
        orderMain.setOrderId(1L);
        orderMain.setOrderStatus(OrderStatusEnum.NEW.getCode());

        ProductInOrder productInOrder = new ProductInOrder();
        productInOrder.setProductId("1");
        productInOrder.setCount(10);

        Set<ProductInOrder> set = new HashSet<>();
        set.add(productInOrder);

        orderMain.setProducts(set);

        productInfo = new ProductInfo();
        productInfo.setProductStock(10);
    }

    @Test
    public void finishSuccessTest() {
        when(orderRepository.findByOrderId(orderMain.getOrderId())).thenReturn(orderMain);

        OrderMain orderMainReturn = orderService.finish(orderMain.getOrderId());

        assertThat(orderMainReturn.getOrderId(), is(orderMain.getOrderId()));
        assertThat(orderMainReturn.getOrderStatus(), is(OrderStatusEnum.FINISHED.getCode()));
        verify(orderRepository).save(orderMain);
    }

    @Test(expected = MyException.class)
    public void finishStatusCanceledTest() {
        orderMain.setOrderStatus(OrderStatusEnum.CANCELED.getCode());

        when(orderRepository.findByOrderId(orderMain.getOrderId()))
                .thenReturn(orderMain);

        orderService.finish(orderMain.getOrderId());
    }

    @Test(expected = MyException.class)
    public void finishStatusFinishedTest() {
        orderMain.setOrderStatus(OrderStatusEnum.FINISHED.getCode());

        when(orderRepository.findByOrderId(orderMain.getOrderId()))
                .thenReturn(orderMain);

        orderService.finish(orderMain.getOrderId());
    }

    @Test
    public void cancelSuccessTest() {
        when(orderRepository.findByOrderId(orderMain.getOrderId()))
                .thenReturn(orderMain);

        when(productInfoRepository.findByProductId("1"))
                .thenReturn(productInfo);

        OrderMain orderMainReturn =
                orderService.cancel(orderMain.getOrderId());

        assertThat(orderMainReturn.getOrderId(),
                is(orderMain.getOrderId()));

        assertThat(orderMainReturn.getOrderStatus(),
                is(OrderStatusEnum.CANCELED.getCode()));

        assertThat(orderMainReturn.getProducts().iterator().next().getCount(),
                is(10));

        verify(productService).increaseStock("1", 10);
    }
    @Test
    public void cancelNoProduct() {
        when(orderRepository.findByOrderId(orderMain.getOrderId())).thenReturn(orderMain);
        orderMain.setProducts(new HashSet<>());

        OrderMain orderMainReturn = orderService.cancel(orderMain.getOrderId());

        assertThat(orderMainReturn.getOrderId(), is(orderMain.getOrderId()));
        assertThat(orderMainReturn.getOrderStatus(), is(OrderStatusEnum.CANCELED.getCode()));
    }

    @Test(expected = MyException.class)
    public void cancelStatusCanceledTest() {
        orderMain.setOrderStatus(OrderStatusEnum.CANCELED.getCode());

        when(orderRepository.findByOrderId(orderMain.getOrderId())).thenReturn(orderMain);

        orderService.cancel(orderMain.getOrderId());
    }

    @Test(expected = MyException.class)
    public void cancelStatusFinishTest() {
        orderMain.setOrderStatus(OrderStatusEnum.FINISHED.getCode());

        when(orderRepository.findByOrderId(orderMain.getOrderId())).thenReturn(orderMain);

        orderService.cancel(orderMain.getOrderId());
    }

    // ===== Bo sung theo yeu cau ticket: truong hop orderId khong ton tai =====

    @Test(expected = MyException.class)
    public void finishOrderNotFoundTest() {
        when(orderRepository.findByOrderId(999L)).thenReturn(null);

        orderService.finish(999L);
    }

    @Test(expected = MyException.class)
    public void cancelOrderNotFoundTest() {
        when(orderRepository.findByOrderId(999L)).thenReturn(null);

        orderService.cancel(999L);
    }

    // Bo sung: cac ham findAll/findByStatus/findByBuyerEmail/findByBuyerPhone
     @Test
    public void findAllTest() {
        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(0, 10);
        org.springframework.data.domain.Page<OrderMain> page =
                new org.springframework.data.domain.PageImpl<>(java.util.List.of(orderMain));

        when(orderRepository.findAllByOrderByOrderStatusAscCreateTimeDesc(pageable)).thenReturn(page);

        org.springframework.data.domain.Page<OrderMain> result = orderService.findAll(pageable);

        assertThat(result.getContent().get(0).getOrderId(), is(orderMain.getOrderId()));
    }

    @Test
    public void findByBuyerEmailTest() {
        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(0, 10);
        org.springframework.data.domain.Page<OrderMain> page =
                new org.springframework.data.domain.PageImpl<>(java.util.List.of(orderMain));

        when(orderRepository.findAllByBuyerEmailOrderByOrderStatusAscCreateTimeDesc("email@email.com", pageable))
                .thenReturn(page);

        org.springframework.data.domain.Page<OrderMain> result =
                orderService.findByBuyerEmail("email@email.com", pageable);

        assertThat(result.getContent().get(0).getOrderId(), is(orderMain.getOrderId()));
    }
    @Test
    public void findByStatusTest() {
        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(0, 10);

        org.springframework.data.domain.Page<OrderMain> page =
                new org.springframework.data.domain.PageImpl<>(
                        java.util.List.of(orderMain)
                );

        when(orderRepository.findAllByOrderStatusOrderByCreateTimeDesc(
                OrderStatusEnum.NEW.getCode(), pageable
        )).thenReturn(page);

        org.springframework.data.domain.Page<OrderMain> result =
                orderService.findByStatus(
                        OrderStatusEnum.NEW.getCode(),
                        pageable
                );

        assertThat(result.getContent().get(0).getOrderId(),
                is(orderMain.getOrderId()));
    }
    @Test
    public void findByBuyerPhoneTest() {
        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(0, 10);

        org.springframework.data.domain.Page<OrderMain> page =
                new org.springframework.data.domain.PageImpl<>(
                        java.util.List.of(orderMain)
                );

        when(orderRepository.findAllByBuyerPhoneOrderByOrderStatusAscCreateTimeDesc(
                "0123456789", pageable
        )).thenReturn(page);

        org.springframework.data.domain.Page<OrderMain> result =
                orderService.findByBuyerPhone(
                        "0123456789",
                        pageable
                );

        assertThat(result.getContent().get(0).getOrderId(),
                is(orderMain.getOrderId()));
    }
}


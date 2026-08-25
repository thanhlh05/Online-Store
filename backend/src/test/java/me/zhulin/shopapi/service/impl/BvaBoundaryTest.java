package me.zhulin.shopapi.service.impl;

import me.zhulin.shopapi.entity.ProductInOrder;
import me.zhulin.shopapi.entity.ProductInfo;
import me.zhulin.shopapi.entity.User;
import me.zhulin.shopapi.repository.ProductInOrderRepository;
import me.zhulin.shopapi.repository.ProductInfoRepository;
import me.zhulin.shopapi.repository.UserRepository;
import me.zhulin.shopapi.repository.CartRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import me.zhulin.shopapi.service.CategoryService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class BvaBoundaryTest {

    private static Validator validator;

    // ===== USER SERVICE =====
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    // ===== CART SERVICE =====
    @Mock
    private ProductInOrderRepository productInOrderRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    // ===== PRODUCT SERVICE =====
    @Mock
    private ProductInfoRepository productInfoRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductServiceImpl productService;

    @Before
    public void setUpValidator() {
        validator = Validation
                .buildDefaultValidatorFactory()
                .getValidator();
    }

    // =========================================================
    // 1. USER - PASSWORD
    // BVA: 2 chars (below boundary) / 3 chars (boundary)
    // =========================================================

    @Test
    public void bvaPassword2CharsRejectedByAnnotationTest() {
        User user = new User();
        user.setPassword("12");

        Set<ConstraintViolation<User>> violations =
                validator.validateProperty(user, "password");

        assertFalse(
                "Password 2 ky tu phai vi pham @Size(min=3)",
                violations.isEmpty()
        );
    }

    @Test
    public void bvaPassword3CharsAcceptedByAnnotationTest() {
        User user = new User();
        user.setPassword("123");

        Set<ConstraintViolation<User>> violations =
                validator.validateProperty(user, "password");

        assertTrue(
                "Password 3 ky tu phai qua duoc @Size(min=3)",
                violations.isEmpty()
        );
    }

    @Test
    public void bvaPassword2CharsAcceptedByServiceDirectlyTest() {
        User user = new User();
        user.setEmail("bva2@test.com");
        user.setPassword("12");

        Mockito.when(passwordEncoder.encode("12"))
                .thenReturn("encoded12");

        Mockito.when(userRepository.save(user))
                .thenReturn(user);

        User saved = userService.save(user);

        assertNotNull(saved);
        assertEquals("encoded12", saved.getPassword());
    }

    // =========================================================
    // 2. CART - QUANTITY
    // BVA: quantity = 0 / quantity = 1
    // =========================================================

    @Test
    public void bvaQuantityZeroAcceptedByServiceTest() {
        User user = new User();

        ProductInOrder item = new ProductInOrder();
        item.setProductId("D0002");
        item.setCount(0);

        Set<ProductInOrder> set = new HashSet<>();
        set.add(item);

        me.zhulin.shopapi.entity.Cart cart =
                new me.zhulin.shopapi.entity.Cart();

        cart.setProducts(new HashSet<>());
        user.setCart(cart);

        cartService.mergeLocalCart(set, user);

        Mockito.verify(
                productInOrderRepository,
                Mockito.times(1)
        ).save(Mockito.any());
    }

    @Test
    public void bvaQuantityOneAcceptedByServiceTest() {
        User user = new User();

        ProductInOrder item = new ProductInOrder();
        item.setProductId("D0002");
        item.setCount(1);

        Set<ProductInOrder> set = new HashSet<>();
        set.add(item);

        me.zhulin.shopapi.entity.Cart cart =
                new me.zhulin.shopapi.entity.Cart();

        cart.setProducts(new HashSet<>());
        user.setCart(cart);

        cartService.mergeLocalCart(set, user);

        Mockito.verify(
                productInOrderRepository,
                Mockito.times(1)
        ).save(Mockito.any());
    }

    // =========================================================
    // 3. PRODUCT - STOCK
    // BVA: -1 / 0
    // =========================================================

    @Test
    public void bvaStockMinus1RejectedByAnnotationTest() {
        ProductInfo p = new ProductInfo();
        p.setProductStock(-1);

        Set<ConstraintViolation<ProductInfo>> violations =
                validator.validateProperty(p, "productStock");

        assertFalse(
                "Stock -1 phai vi pham @Min(0)",
                violations.isEmpty()
        );
    }

    @Test
    public void bvaStock0AcceptedByAnnotationTest() {
        ProductInfo p = new ProductInfo();
        p.setProductStock(0);

        Set<ConstraintViolation<ProductInfo>> violations =
                validator.validateProperty(p, "productStock");

        assertTrue(
                "Stock 0 phai qua duoc @Min(0)",
                violations.isEmpty()
        );
    }

    @Test
    public void bvaStockMinus1AcceptedByServiceDirectlyTest() {
        /*
         * WHITE-BOX:
         * ProductService.save() -> update()
         *
         * update() KHONG co kiem tra:
         * productStock < 0
         *
         * Vi vay khi goi truc tiep Service,
         * stock = -1 van duoc luu.
         */

        ProductInfo p = new ProductInfo();

        p.setProductId("BVA-STOCK");
        p.setProductStock(-1);
        p.setProductPrice(new BigDecimal("40.00"));

        // Can categoryType de Service di qua update()
        p.setCategoryType(1);

        // BAT BUOC: productStatus la Integer, mac dinh null khi new().
        // update() co dong "if (productInfo.getProductStatus() > 1)" -
        // neu khong set truoc, unbox null se nem NullPointerException.
        p.setProductStatus(0);

        // Mock dependency duoc goi trong ProductServiceImpl.update()
        Mockito.when(categoryService.findByCategoryType(1))
                .thenReturn(null);

        Mockito.when(productInfoRepository.save(p))
                .thenReturn(p);

        ProductInfo saved = productService.save(p);

        assertNotNull(saved);

        assertEquals(
                Integer.valueOf(-1),
                saved.getProductStock()
        );

        Mockito.verify(
                productInfoRepository,
                Mockito.times(1)
        ).save(p);
    }

    // =========================================================
    // 4. PRODUCT - PRICE
    // BVA: price = -1
    // =========================================================

    @Test
    public void bvaPriceMinus1NoConstraintAtAllTest() {
        /*
         * WHITE-BOX:
         * ProductInfo.productPrice khong co @Min.
         *
         * Vi vay Bean Validation khong phat hien
         * productPrice = -1.
         */

        ProductInfo p = new ProductInfo();

        p.setProductPrice(new BigDecimal("-1"));

        Set<ConstraintViolation<ProductInfo>> violations =
                validator.validateProperty(
                        p,
                        "productPrice"
                );

        assertTrue(
                "BUG: productPrice = -1 khong bi validation chan",
                violations.isEmpty()
        );
    }

    @Test
    public void bvaPriceMinus1AcceptedByServiceTest() {
        /*
         * WHITE-BOX:
         *
         * ProductService.save()
         *        ↓
         * update()
         *        ↓
         * Khong co check productPrice < 0
         *        ↓
         * productInfoRepository.save()
         *
         * KET QUA THUC TE:
         * productPrice = -1 van duoc luu.
         */

        ProductInfo p = new ProductInfo();

        p.setProductId("BVA-PRICE");
        p.setProductPrice(new BigDecimal("-1"));
        p.setProductStock(50);

        // Can categoryType de update() chay binh thuong
        p.setCategoryType(1);

        // BAT BUOC: tranh NullPointerException o dong "getProductStatus() > 1"
        p.setProductStatus(0);

        // Mock CategoryService duoc goi trong update()
        Mockito.when(categoryService.findByCategoryType(1))
                .thenReturn(null);

        // Mock repository save
        Mockito.when(productInfoRepository.save(p))
                .thenReturn(p);

        ProductInfo saved = productService.save(p);

        assertNotNull(saved);

        assertEquals(
                new BigDecimal("-1"),
                saved.getProductPrice()
        );

        Mockito.verify(
                productInfoRepository,
                Mockito.times(1)
        ).save(p);
    }
}
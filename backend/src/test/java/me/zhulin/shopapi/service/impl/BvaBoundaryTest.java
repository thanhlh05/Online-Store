package me.zhulin.shopapi.service.impl;

import me.zhulin.shopapi.entity.ProductInfo;
import me.zhulin.shopapi.entity.User;
import me.zhulin.shopapi.form.ItemForm;
import me.zhulin.shopapi.validation.OnCreate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Set;

public class BvaBoundaryTest {

    private Validator validator;
    private ProductInfo validProduct() {
        ProductInfo product = new ProductInfo();

        product.setProductId("ABC");
        product.setProductName("ABC");
        product.setProductPrice(new BigDecimal("1.00"));
        product.setProductStock(1);
        product.setProductDescription("");

        return product;
    }

    private User validUser() {
        User user = new User();

        user.setEmail("a@b.co");
        user.setPassword("abc");
        user.setName("Test User");
        user.setPhone("0123456789");
        user.setAddress("Test Address");

        return user;
    }

    private ItemForm validItemForm() {
        ItemForm form = new ItemForm();

        form.setProductId("ABC");
        form.setQuantity(1);

        return form;
    }
    @Before
    public void setUp() {
        ValidatorFactory factory =
                Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // =========================================================
    // PASSWORD: @Size(min = 3, max = 20, groups = OnCreate.class)
    // =========================================================

    @Test
    public void password2CharsInvalidTest() {
        User user = new User();
        user.setPassword("ab");

        Set<ConstraintViolation<User>> violations =
                validator.validate(user, OnCreate.class);

        Assert.assertFalse(violations.isEmpty());
    }

    @Test
    public void password3CharsValidTest() {
        User user = new User();
        user.setPassword("abc");

        Set<ConstraintViolation<User>> violations =
                validator.validate(user, OnCreate.class);

        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void password4CharsValidTest() {
        User user = new User();
        user.setPassword("abcd");

        Set<ConstraintViolation<User>> violations =
                validator.validate(user, OnCreate.class);

        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void password19CharsValidTest() {
        User user = new User();
        user.setPassword("abcdefghijklmnopqrs");

        Set<ConstraintViolation<User>> violations =
                validator.validate(user, OnCreate.class);

        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void password20CharsValidTest() {
        User user = new User();
        user.setPassword("abcdefghijklmnopqrst");

        Set<ConstraintViolation<User>> violations =
                validator.validate(user, OnCreate.class);

        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void password21CharsInvalidTest() {
        User user = new User();
        user.setPassword("abcdefghijklmnopqrstu");

        Set<ConstraintViolation<User>> violations =
                validator.validate(user, OnCreate.class);

        Assert.assertFalse(violations.isEmpty());
    }

    // =========================================================
    // EMAIL: @Size(min = 6, max = 50) + @Email
    // =========================================================

    @Test
    public void email5CharsInvalidTest() {
        User user = new User();
        user.setEmail("a@b.c");

        Set<ConstraintViolation<User>> violations =
                validator.validate(user);

        Assert.assertFalse(violations.isEmpty());
    }

    @Test
    public void email6CharsValidTest() {
        User user = validUser();
        user.setEmail("a@b.co");

        Set<ConstraintViolation<User>> violations =
                validator.validate(user);

        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void email7CharsValidTest() {
        User user = validUser();
        user.setEmail("a@bc.co");

        Set<ConstraintViolation<User>> violations =
                validator.validate(user);

        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void email49CharsValidTest() {
        User user = validUser();

        String email = "a" + "a".repeat(43) + "@b.co";
        user.setEmail(email);

        Set<ConstraintViolation<User>> violations =
                validator.validate(user);

        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void email50CharsValidTest() {
        User user = validUser();

        String email = "a" + "a".repeat(44) + "@b.co";
        user.setEmail(email);

        Set<ConstraintViolation<User>> violations =
                validator.validate(user);

        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void email51CharsInvalidTest() {
        User user = new User();
        user.setEmail(repeat("a", 46) + "@b.co");

        Set<ConstraintViolation<User>> violations =
                validator.validate(user);

        Assert.assertFalse(violations.isEmpty());
    }

    @Test
    public void emailInvalidFormatTest() {
        User user = new User();
        user.setEmail("abc");

        Set<ConstraintViolation<User>> violations =
                validator.validate(user);

        Assert.assertFalse(violations.isEmpty());
    }

    // =========================================================
    // PRODUCT PRICE: @NotNull + @Positive
    // =========================================================

    @Test
    public void productPriceMinus1InvalidTest() {
        ProductInfo product = new ProductInfo();
        product.setProductPrice(new BigDecimal("-1"));

        Set<ConstraintViolation<ProductInfo>> violations =
                validator.validate(product);

        Assert.assertFalse(violations.isEmpty());
    }

    @Test
    public void productPriceZeroInvalidTest() {
        ProductInfo product = new ProductInfo();
        product.setProductPrice(BigDecimal.ZERO);

        Set<ConstraintViolation<ProductInfo>> violations =
                validator.validate(product);

        Assert.assertFalse(violations.isEmpty());
    }

    @Test
    public void productPricePoint01ValidTest() {
        ProductInfo product = validProduct();
        product.setProductPrice(new BigDecimal("0.01"));

        Set<ConstraintViolation<ProductInfo>> violations =
                validator.validate(product);

        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void productPriceNullInvalidTest() {
        ProductInfo product = new ProductInfo();
        product.setProductPrice(null);

        Set<ConstraintViolation<ProductInfo>> violations =
                validator.validate(product);

        Assert.assertFalse(violations.isEmpty());
    }

    // =========================================================
    // PRODUCT STOCK: @NotNull + @Min(0)
    // =========================================================

    @Test
    public void productStockMinus1InvalidTest() {
        ProductInfo product = new ProductInfo();
        product.setProductStock(-1);

        Set<ConstraintViolation<ProductInfo>> violations =
                validator.validate(product);

        Assert.assertFalse(violations.isEmpty());
    }

    @Test
    public void productStockZeroValidTest() {
        ProductInfo product = validProduct();
        product.setProductStock(0);

        Set<ConstraintViolation<ProductInfo>> violations =
                validator.validate(product);

        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void productStockOneValidTest() {
        ProductInfo product = validProduct();
        product.setProductStock(1);

        Set<ConstraintViolation<ProductInfo>> violations =
                validator.validate(product);

        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void productStockNullInvalidTest() {
        ProductInfo product = new ProductInfo();
        product.setProductStock(null);

        Set<ConstraintViolation<ProductInfo>> violations =
                validator.validate(product);

        Assert.assertFalse(violations.isEmpty());
    }

    // =========================================================
    // QUANTITY: @Min(1)
    // =========================================================

    @Test
    public void quantityMinus1InvalidTest() {
        ItemForm form = new ItemForm();
        form.setQuantity(-1);

        Set<ConstraintViolation<ItemForm>> violations =
                validator.validate(form);

        Assert.assertFalse(violations.isEmpty());
    }

    @Test
    public void quantityZeroInvalidTest() {
        ItemForm form = new ItemForm();
        form.setQuantity(0);

        Set<ConstraintViolation<ItemForm>> violations =
                validator.validate(form);

        Assert.assertFalse(violations.isEmpty());
    }

    @Test
    public void quantityOneValidTest() {
        ItemForm form = new ItemForm();
        form.setQuantity(1);

        Set<ConstraintViolation<ItemForm>> violations =
                validator.validate(form);

        Assert.assertFalse(
                violations.stream()
                        .anyMatch(v -> "quantity".equals(v.getPropertyPath().toString()))
        );
    }

    @Test
    public void quantityTwoValidTest() {
        ItemForm form = new ItemForm();
        form.setQuantity(2);

        Set<ConstraintViolation<ItemForm>> violations =
                validator.validate(form);

        Assert.assertFalse(
                violations.stream()
                        .anyMatch(v -> "quantity".equals(v.getPropertyPath().toString()))
        );
    }

    @Test
    public void quantityNullInvalidTest() {
        ItemForm form = new ItemForm();
        form.setQuantity(null);

        Set<ConstraintViolation<ItemForm>> violations =
                validator.validate(form);

        Assert.assertFalse(
                violations.stream()
                        .anyMatch(v -> "quantity".equals(v.getPropertyPath().toString()))
        );
    }

    // =========================================================
    // PRODUCT ID: @Size(min = 3, max = 30)
    // =========================================================

    @Test
    public void productId2CharsInvalidTest() {
        ProductInfo product = new ProductInfo();
        product.setProductId("AB");

        Set<ConstraintViolation<ProductInfo>> violations =
                validator.validate(product);

        Assert.assertFalse(violations.isEmpty());
    }

    @Test
    public void productId3CharsValidTest() {
        ProductInfo product = validProduct();
        product.setProductId("ABC");

        Set<ConstraintViolation<ProductInfo>> violations =
                validator.validate(product);

        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void productId30CharsValidTest() {
        ProductInfo product = validProduct();
        product.setProductId("123456789012345678901234567890");

        Set<ConstraintViolation<ProductInfo>> violations =
                validator.validate(product);

        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void productId31CharsInvalidTest() {
        ProductInfo product = new ProductInfo();
        product.setProductId(repeat("A", 31));

        Set<ConstraintViolation<ProductInfo>> violations =
                validator.validate(product);

        Assert.assertFalse(violations.isEmpty());
    }

    // =========================================================
    // PRODUCT NAME: @Size(min = 3, max = 100)
    // =========================================================

    @Test
    public void productName2CharsInvalidTest() {
        ProductInfo product = new ProductInfo();
        product.setProductName("AB");

        Set<ConstraintViolation<ProductInfo>> violations =
                validator.validate(product);

        Assert.assertFalse(violations.isEmpty());
    }

    @Test
    public void productName3CharsValidTest() {
        ProductInfo product = validProduct();
        product.setProductName("ABC");

        Set<ConstraintViolation<ProductInfo>> violations =
                validator.validate(product);

        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void productName100CharsValidTest() {
        ProductInfo product = validProduct();
        product.setProductName("A".repeat(100));

        Set<ConstraintViolation<ProductInfo>> violations =
                validator.validate(product);

        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void productName101CharsInvalidTest() {
        ProductInfo product = new ProductInfo();
        product.setProductName(repeat("A", 101));

        Set<ConstraintViolation<ProductInfo>> violations =
                validator.validate(product);

        Assert.assertFalse(violations.isEmpty());
    }

    // =========================================================
    // PRODUCT DESCRIPTION: @Size(max = 200)
    // =========================================================

    @Test
    public void productDescriptionEmptyValidTest() {
        ProductInfo product = validProduct();
        product.setProductDescription("");

        Set<ConstraintViolation<ProductInfo>> violations =
                validator.validate(product);

        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void productDescription200CharsValidTest() {
        ProductInfo product = validProduct();
        product.setProductDescription("A".repeat(200));

        Set<ConstraintViolation<ProductInfo>> violations =
                validator.validate(product);

        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void productDescription201CharsInvalidTest() {
        ProductInfo product = new ProductInfo();
        product.setProductDescription(repeat("A", 201));

        Set<ConstraintViolation<ProductInfo>> violations =
                validator.validate(product);

        Assert.assertFalse(violations.isEmpty());
    }

    // =========================================================
    // ITEM FORM: PRODUCT ID + QUANTITY
    // =========================================================

    @Test
    public void itemFormValidCombinationTest() {
        ItemForm form = new ItemForm();
        form.setProductId("ABC");
        form.setQuantity(1);

        Set<ConstraintViolation<ItemForm>> violations =
                validator.validate(form);

        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void itemFormInvalidProductIdTest() {
        ItemForm form = new ItemForm();
        form.setProductId("AB");
        form.setQuantity(1);

        Set<ConstraintViolation<ItemForm>> violations =
                validator.validate(form);

        Assert.assertFalse(violations.isEmpty());
    }

    @Test
    public void itemFormInvalidQuantityTest() {
        ItemForm form = new ItemForm();
        form.setProductId("ABC");
        form.setQuantity(0);

        Set<ConstraintViolation<ItemForm>> violations =
                validator.validate(form);

        Assert.assertFalse(violations.isEmpty());
    }

    private String repeat(String value, int count) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < count; i++) {
            builder.append(value);
        }

        return builder.toString();
    }
}
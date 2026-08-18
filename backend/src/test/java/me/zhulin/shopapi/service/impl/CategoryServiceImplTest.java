package me.zhulin.shopapi.service.impl;

import me.zhulin.shopapi.entity.ProductCategory;
import me.zhulin.shopapi.exception.MyException;
import me.zhulin.shopapi.repository.ProductCategoryRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class CategoryServiceImplTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    // 1. Tìm category thành công
    @Test
    public void findByCategoryTypeSuccessTest() {
        ProductCategory category = new ProductCategory();
        category.setCategoryId(1);

        Mockito.when(productCategoryRepository.findByCategoryType(1))
                .thenReturn(category);

        ProductCategory result = categoryService.findByCategoryType(1);

        assertNotNull(result);
        assertEquals(Integer.valueOf(1), result.getCategoryId());

        Mockito.verify(productCategoryRepository)
                .findByCategoryType(1);
    }

    // 2. Category không tồn tại -> MyException
    @Test(expected = MyException.class)
    public void findByCategoryTypeNotFoundTest() {
        Mockito.when(productCategoryRepository.findByCategoryType(999))
                .thenReturn(null);

        categoryService.findByCategoryType(999);
    }

    // 3. findAll có dữ liệu
    @Test
    public void findAllSuccessTest() {
        ProductCategory category1 = new ProductCategory();
        category1.setCategoryId(1);

        ProductCategory category2 = new ProductCategory();
        category2.setCategoryId(2);

        List<ProductCategory> categories =
                Arrays.asList(category1, category2);

        Mockito.when(productCategoryRepository.findAllByOrderByCategoryType())
                .thenReturn(categories);

        List<ProductCategory> result = categoryService.findAll();

        assertEquals(2, result.size());
        assertEquals(categories, result);

        Mockito.verify(productCategoryRepository)
                .findAllByOrderByCategoryType();
    }

    // 4. findAll không có category
    @Test
    public void findAllEmptyTest() {
        Mockito.when(productCategoryRepository.findAllByOrderByCategoryType())
                .thenReturn(Collections.emptyList());

        List<ProductCategory> result = categoryService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        Mockito.verify(productCategoryRepository)
                .findAllByOrderByCategoryType();
    }

    // 5. Tìm nhiều category theo danh sách type
    @Test
    public void findByCategoryTypeInSuccessTest() {
        List<Integer> types = Arrays.asList(1, 2);

        ProductCategory category = new ProductCategory();
        category.setCategoryId(1);

        List<ProductCategory> expected =
                Collections.singletonList(category);

        Mockito.when(
                productCategoryRepository
                        .findByCategoryTypeInOrderByCategoryTypeAsc(types)
        ).thenReturn(expected);

        List<ProductCategory> result =
                categoryService.findByCategoryTypeIn(types);

        assertEquals(expected, result);

        Mockito.verify(productCategoryRepository)
                .findByCategoryTypeInOrderByCategoryTypeAsc(types);
    }

    // 6. Danh sách type rỗng
    @Test
    public void findByCategoryTypeInEmptyTest() {
        List<Integer> types = Collections.emptyList();

        Mockito.when(
                productCategoryRepository
                        .findByCategoryTypeInOrderByCategoryTypeAsc(types)
        ).thenReturn(Collections.emptyList());

        List<ProductCategory> result =
                categoryService.findByCategoryTypeIn(types);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        Mockito.verify(productCategoryRepository)
                .findByCategoryTypeInOrderByCategoryTypeAsc(types);
    }

    // 7. Save category
    @Test
    public void saveSuccessTest() {
        ProductCategory category = new ProductCategory();
        category.setCategoryId(1);

        Mockito.when(productCategoryRepository.save(category))
                .thenReturn(category);

        ProductCategory result = categoryService.save(category);

        assertNotNull(result);
        assertEquals(category, result);

        Mockito.verify(productCategoryRepository)
                .save(category);
    }
}
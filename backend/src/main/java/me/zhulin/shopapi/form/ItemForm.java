package me.zhulin.shopapi.form;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
/**
 * Created By Zhu Lin on 3/11/2018.
 */
@Data
public class ItemForm {
    @Min(value = 1)
    private Integer quantity;
    @NotEmpty
    @Size(min = 3, max = 30, message = "Product ID must be between 3 and 30 characters")
    private String productId;
}

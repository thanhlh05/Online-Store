package me.zhulin.shopapi.entity;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created By Zhu Lin on 3/10/2018.
 */
@Entity
@Data
@DynamicUpdate
public class ProductInfo implements Serializable {
    @Id
    @Size(min = 3, max = 30, message = "Product ID must be between 3 and 30 characters")
    private String productId;
    /** 名字. */
    @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
    private String productName;

    /** 单价. */
    @NotNull
    @Positive(message = "Price must be greater than 0")
    private BigDecimal productPrice;

    /** 库存. */
    @NotNull
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer productStock;

    /** 描述. */
    @Size(max = 200, message = "Product description must not exceed 200 characters")
    private String productDescription;

    /** 小图. */
    private String productIcon;

    /** 0: on-sale 1: off-sale */

    @ColumnDefault("0")
    private Integer productStatus;


   /** 类目编号. */
    @ColumnDefault("0")
    private Integer categoryType;

    @CreationTimestamp
    private Date createTime;
    @UpdateTimestamp
    private Date updateTime;

    public ProductInfo() {
    }
}

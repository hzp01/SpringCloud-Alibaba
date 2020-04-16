package cn.hzp.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity(name = "shop_product")
public class Product {
    /**
     * 主键id，生成方式为自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pid;

    private String pname;
    private Double pprice;
    private Integer stock;
}

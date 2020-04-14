package cn.hzp.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "shop-order")
@Data
public class Order {
    /**
     * 主键id，生成方式为自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer oid;

    private Integer uid;
    private String uname;

    private Integer pid;
    private String pname;
    private Double pprice;

    /**
     * 购买数量
     */
    private Integer number;
}

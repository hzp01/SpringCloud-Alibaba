package cn.hzp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "shop_order")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

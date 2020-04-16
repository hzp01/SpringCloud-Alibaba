package cn.hzp.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "shop_user")
@Data
public class User {
    /**
     * 主键id，生成方式为自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer uid;

    private String username;
    private String password;
    private String tel;
}

package cn.hzp.dao;

import cn.hzp.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductDao extends JpaRepository<Product, Integer> {
}

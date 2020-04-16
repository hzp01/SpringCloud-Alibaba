package cn.hzp.dao;

import cn.hzp.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderDao extends JpaRepository<Order, Integer> {
}

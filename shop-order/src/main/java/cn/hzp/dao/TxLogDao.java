package cn.hzp.dao;

import cn.hzp.domain.TxLog;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TxLogDao extends JpaRepository<TxLog, String> {
}

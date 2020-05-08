package cn.hzp.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity(name = "shop_txlog")
public class TxLog {
    @Id
    private String txId;
    private Date date;
}

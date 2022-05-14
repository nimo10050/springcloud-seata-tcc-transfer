
package com.nimo.transfer.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 账户
 *
 * @author nimo
 */
@Getter
@Setter
public class Account {

    /**
     * 主键
     */
    private Long id;

    /**
     * 账户
     */
    private String accountNo;
    /**
     * 余额
     */
    private double amount;
    /**
     * 冻结金额
     */
    private double freezedAmount;

}

/*
 *  Copyright 1999-2021 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.nimo.transfer.service.impl;

import com.nimo.transfer.mapper.AccountMapper;
import com.nimo.transfer.model.Account;
import com.nimo.transfer.service.TransferInService;
import io.seata.rm.tcc.api.BusinessActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 扣钱参与者实现
 *
 * @author nimo10050
 */
@Service
public class TransferInServiceImpl implements TransferInService {

    /**
     * 扣钱账户 DAO
     */
    @Autowired
    private AccountMapper accountMapper;

    /**
     * 扣钱数据源事务模板
     */
    @Autowired
    private TransactionTemplate toDsTransactionTemplate;

    /**
     * 一阶段准备，转入资金 准备
     *
     * @param accountNo
     * @param amount
     * @return
     */
    @Override
    public boolean prepareAdd(BusinessActionContext businessActionContext,final String accountNo,
                              final double amount) {
        return toDsTransactionTemplate.execute(new TransactionCallback<Boolean>() {

            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                try {
                    // 校验账户
                    Account account = accountMapper.getAccountForUpdate(accountNo);
                    if (account == null) {
                        System.out.println(
                                "prepareAdd: 账户[" + accountNo + "]不存在, txId:");
                        return false;
                    }
                    // 待转入资金作为 不可用金额
                    double freezedAmount = account.getFreezedAmount() + amount;
                    account.setFreezedAmount(freezedAmount);
                    accountMapper.updateFreezedAmount(account);
                    return true;
                } catch (Throwable t) {
                    t.printStackTrace();
                    status.setRollbackOnly();
                    return false;
                }
            }
        });
    }

    /**
     * 二阶段提交
     *
     * @param businessActionContext
     * @return
     */
    @Override
    public boolean commit(BusinessActionContext businessActionContext) {
        //分布式事务ID
        final String xid = businessActionContext.getXid();
        //账户ID
        final String accountNo = String.valueOf(businessActionContext.getActionContext("accountNo"));
        //转出金额
        final double amount = Double.valueOf(String.valueOf(businessActionContext.getActionContext("amount")));
        return toDsTransactionTemplate.execute(new TransactionCallback<Boolean>() {

            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                try {
                    Account account = accountMapper.getAccountForUpdate(accountNo);
                    //加钱
                    double newAmount = account.getAmount() + amount;
                    account.setAmount(newAmount);
                    //冻结金额 清除
                    account.setFreezedAmount(account.getFreezedAmount() - amount);
                    accountMapper.updateAmount(account);

                    System.out.println(
                            String.format("add account[%s] amount[%f], dtx transaction id: %s.", accountNo, amount, xid));
                    return true;
                } catch (Throwable t) {
                    t.printStackTrace();
                    status.setRollbackOnly();
                    return false;
                }
            }
        });

    }

    /**
     * 二阶段回滚
     *
     * @param businessActionContext
     * @return
     */
    @Override
    public boolean rollback(BusinessActionContext businessActionContext) {
        //分布式事务ID
        final String xid = businessActionContext.getXid();
        //账户ID
        final String accountNo = String.valueOf(businessActionContext.getActionContext("accountNo"));
        //转出金额
        final double amount = Double.valueOf(String.valueOf(businessActionContext.getActionContext("amount")));
        return toDsTransactionTemplate.execute(new TransactionCallback<Boolean>() {

            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                try {
                    Account account = accountMapper.getAccountForUpdate(accountNo);
                    if (account == null) {
                        //账户不存在, 无需回滚动作
                        return true;
                    }
                    //冻结金额 清除
                    account.setFreezedAmount(account.getFreezedAmount() - amount);
                    accountMapper.updateFreezedAmount(account);

                    System.out.println(String
                            .format("Undo prepareAdd account[%s] amount[%f], dtx transaction id: %s.", accountNo, amount,
                                    xid));
                    return true;
                } catch (Throwable t) {
                    t.printStackTrace();
                    status.setRollbackOnly();
                    return false;
                }
            }
        });
    }

}

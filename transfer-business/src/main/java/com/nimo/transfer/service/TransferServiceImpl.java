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
package com.nimo.transfer.service;

import com.nimo.transfer.client.TransferInClient;
import com.nimo.transfer.client.TransferOutClient;
import com.nimo.transfer.service.impl.TransferService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 转账服务实现
 *
 * @author nimo10050
 */
@Service
public class TransferServiceImpl implements TransferService {

    @Autowired
    private TransferInClient transferInClient;

    @Autowired
    private TransferOutClient transferOutClient;


    /**
     * 转账操作
     *
     * @param from   扣钱账户
     * @param to     加钱账户
     * @param amount 转账金额
     * @return
     */
    @Override
    @GlobalTransactional
    public boolean transfer(final String from, final String to, final double amount) {
        // 扣钱参与者，一阶段执行
        String outResult = transferOutClient.transferOut(from, amount);
        if (!"success".equals(outResult)) {
            throw new RuntimeException("账号:[" + from + "] 预扣款失败");
        }

        // 加钱参与者，一阶段执行
        String inResult = transferInClient.transferIn(to, amount);
        if (!"success".equals(inResult)) {
            throw new RuntimeException("账号:[" + to + "] 预收款失败");
        }


        System.out.println(
                String.format("transfer amount[%s] from [%s] to [%s] finish.", String.valueOf(amount), from, to));
        return true;
    }

}

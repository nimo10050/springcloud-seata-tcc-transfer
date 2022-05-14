package com.nimo.transfer.controller;

import com.nimo.transfer.service.TransferServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @auther nimo10050
 * @desc 转账交易入口
 * @date 2022/5/14
 */
@RestController
public class TransferController {

    @Autowired
    private TransferServiceImpl transferService;

    @PostMapping("/transfer")
    public String transfer(String fromAccount, String toAccount, double amount) {
        if (transferService.transfer(fromAccount, toAccount, amount)) {
            return "success";
        }
        return "fail";
    }
}

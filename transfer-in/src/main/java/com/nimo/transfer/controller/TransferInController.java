package com.nimo.transfer.controller;

import com.nimo.transfer.service.TransferInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @auther zgp
 * @desc 账户接口
 * @date 2022/5/14
 */
@RestController
public class TransferInController {

    @Autowired
    private TransferInService transferService;

    @PostMapping("/transferIn")
    public String transferIn(String toAccount, double amount) {
        if (transferService.prepareAdd(null, toAccount, amount)) {
            return "success";
        }
        return "fail";
    }
}
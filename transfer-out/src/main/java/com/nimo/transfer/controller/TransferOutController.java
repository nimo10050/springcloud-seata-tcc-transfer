package com.nimo.transfer.controller;

import com.nimo.transfer.service.TransferOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @auther zgp
 * @desc 扣减账户接口
 * @date 2022/5/14
 */
@RestController
public class TransferOutController {

    @Autowired
    private TransferOutService transferService;

    @PostMapping("/transferOut")
    public String transferOut(String fromAccount, double amount) {
        if (transferService.prepareMinus(null, fromAccount, amount)) {
            return "success";
        }
        return "fail";
    }
}
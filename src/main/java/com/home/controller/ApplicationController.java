package com.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class ApplicationController {

    public static final int WAIT_BEFORE_SHUTDOWN = 5000;

    @RequestMapping(method = POST, value = "/shutdown")
    private String shutdown() {
        new Thread(() -> {
            try {
                Thread.sleep(WAIT_BEFORE_SHUTDOWN);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }).start();
        return "close_tab";
    }
}

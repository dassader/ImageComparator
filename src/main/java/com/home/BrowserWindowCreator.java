package com.home;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.net.URI;

@Component
public class BrowserWindowCreator implements CommandLineRunner {
    @Override
    public void run(String... strings) throws Exception {
        Runtime.getRuntime().exec("cmd.exe /c start http://localhost:8080");
    }
}

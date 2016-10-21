package com.home.controller;

import com.home.service.ImageActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/image")
public class ImageActionController {
    @Autowired
    private ImageActionService imageActionService;

    @RequestMapping(method = POST, value = "/compare")
    private String compare(@RequestParam("firstImage") MultipartFile firstImage,
                           @RequestParam("secondImage") MultipartFile secondImage,
                           Model model) throws IOException {

        if (firstImage.getSize() == 0 || secondImage.getSize() == 0) {
            return "index";
        }

        model.addAttribute("image_base_64", imageActionService.compare(firstImage.getInputStream(),
                secondImage.getInputStream()));

        return "result";
    }
}

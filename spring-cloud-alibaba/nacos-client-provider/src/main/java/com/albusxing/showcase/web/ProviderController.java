package com.albusxing.showcase.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Albusxing
 */
@RestController
public class ProviderController {

    @GetMapping("/provider/msg")
    public String getMessage() {
        return "this message is from provider service";
    }

}

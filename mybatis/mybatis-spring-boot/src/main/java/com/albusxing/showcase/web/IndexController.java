package com.albusxing.showcase.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liguoqing
 * @date 2019-08-22
 */
@RestController
public class IndexController {

	@GetMapping("/")
	public String home() {
		return "Hello World!";
	}

}

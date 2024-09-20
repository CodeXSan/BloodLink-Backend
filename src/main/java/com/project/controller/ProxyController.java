package com.project.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ProxyController {
	 @Value("${nominatim.url}")
	    private String nominatimUrl;

	    @GetMapping("/api/geocode")
	    public String proxyGeocode(@RequestParam double lat, @RequestParam double lon) {
	        RestTemplate restTemplate = new RestTemplate();
	        String url = String.format(nominatimUrl + "?format=json&lat=%f&lon=%f", lat, lon);
	        return restTemplate.getForObject(url, String.class);
	    }
}

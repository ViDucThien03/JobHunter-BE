package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/")
    @CrossOrigin
    public String getHelloWord() {
        return "Welcom ViDucThien to Spring boot";
    }

    @GetMapping("/abc")
    @CrossOrigin
    public String test() {
        return "Welcom ViDucThien";
    }
}

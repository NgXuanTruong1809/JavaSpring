package vn.hoidanit.laptopshop;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/")
    public String index() {
        return "Hello World from Spring Boot with Xuan Truong!";
    }

    @GetMapping("/user")
    public String userPage() {
        return "Hello World from Spring Boot with USER!";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "Hello World from Spring Boot with ADMIN!";
    }

}

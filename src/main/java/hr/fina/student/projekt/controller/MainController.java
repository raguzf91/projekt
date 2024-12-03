package hr.fina.student.projekt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MainController {

    @GetMapping("/login")
	String login() {
		return "Login";
	}

	@GetMapping("/register")
	String register() {
		return "Register";
	}
}


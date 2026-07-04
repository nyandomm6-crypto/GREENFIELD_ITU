package itu.greenField.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    @GetMapping({"/", "/dashboard"})
    public ModelAndView getDashboard() {
        ModelAndView modelAndView = new ModelAndView("index");
        return modelAndView;
    }
}

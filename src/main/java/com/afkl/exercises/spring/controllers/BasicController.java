package com.afkl.exercises.spring.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/home")
public class BasicController {

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(ModelAndView mv) {
		mv.setViewName("home");
		return mv;
	}

}

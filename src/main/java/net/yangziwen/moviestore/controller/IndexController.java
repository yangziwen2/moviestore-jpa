package net.yangziwen.moviestore.controller;

import net.yangziwen.moviestore.dao.base.DaoConstant;
import net.yangziwen.moviestore.service.IWebsiteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
	
	@Autowired
	private IWebsiteService websiteService;
	
	@RequestMapping({"/", "/index"})
	public String index(Model model) {
		model.addAttribute("websiteList", websiteService.getWebsiteListResult(0, 0, new ModelMap()
			.addAttribute(DaoConstant.ORDER_BY, new ModelMap()
				.addAttribute("rank", DaoConstant.ORDER_DESC)
			)
		));
		return "index";
	}
	
}

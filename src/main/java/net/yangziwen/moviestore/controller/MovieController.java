package net.yangziwen.moviestore.controller;

import java.util.List;
import java.util.Map;

import net.yangziwen.moviestore.dao.base.DaoConstant;
import net.yangziwen.moviestore.pojo.MovieInfo;
import net.yangziwen.moviestore.pojo.Website;
import net.yangziwen.moviestore.service.IMovieInfoService;
import net.yangziwen.moviestore.service.IWebsiteService;
import net.yangziwen.moviestore.util.CommonConstant;
import net.yangziwen.moviestore.util.Page;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/movie")
public class MovieController {
	
	@Autowired
	private IWebsiteService websiteService;
	@Autowired
	private IMovieInfoService movieInfoService;

	@RequestMapping("/{websiteName}/list")
	public String list(
			@PathVariable("websiteName") String websiteName,
			@RequestParam(defaultValue = CommonConstant.DEFAUTL_START_STR) 
			int start,
			@RequestParam(defaultValue = "48") 
			int limit,
			@RequestParam(required = false)
			String year,
			@RequestParam(required = false)
			String area,
			@RequestParam(required = false) 
			String title,
			@RequestParam(required = false)
			String category,
			@RequestParam(required = false)
			String subcategory,
			@RequestParam(required = false)
			String actor,
			Model model) {
		Website website = websiteService.getWebsiteByName(websiteName);
		ModelMap param = new ModelMap();
		param.addAttribute("websiteId", website.getId());
		if(StringUtils.isNotBlank(year)) {
			param.addAttribute("year", year);
		}
		if(StringUtils.isNotBlank(area)) {
			param.addAttribute("area", area);
		}
		if(StringUtils.isNotBlank(title)) {
			param.addAttribute("title__contain", title);
		}
		if(StringUtils.isNotBlank(category)) {
			param.addAttribute("category", category);
		}
		if(StringUtils.isNotBlank(subcategory)) {
			param.addAttribute("subcategory", subcategory);
		}
		if(StringUtils.isNotBlank(actor)) {
			param.addAttribute("actors__contain", actor);
		}
		param.addAttribute(DaoConstant.ORDER_BY, new ModelMap().addAttribute("movieId", DaoConstant.ORDER_DESC));
		
		Page<MovieInfo> page = movieInfoService.getMovieInfoPaginateResult(start, limit, param);
		model
			.addAttribute("page", page)
			.addAttribute("website", website)
			.addAttribute("yearList", movieInfoService.getMovieInfoYearListByWebsite(website))
			.addAttribute("areaList", movieInfoService.getMovieInfoAreaListByWebsite(website))
			.addAttribute("categoryList", movieInfoService.getMovieInfoCategoryListByWebsite(website));
		return "movie/list";
	}
	
	@ResponseBody
	@RequestMapping("/{websiteName}/listSubcategory")
	public Map<String, Object> listSubcategory(
			@PathVariable("websiteName") String websiteName,
			@RequestParam("category") String category) {
		Website website = websiteService.getWebsiteByName(websiteName);
		if(website == null) {
			return new ModelMap().addAttribute("success", false).addAttribute("message", "网站信息不存在!");
		}
		List<String> subcategoryList = movieInfoService.getMovieInfoSubcategoryListByWebsiteAndCategory(website, category);
		return new ModelMap().addAttribute("success", true).addAttribute("subcategoryList", subcategoryList);
	}
}

package net.yangziwen.moviestore.service.impl;

import java.util.List;
import java.util.Map;

import net.yangziwen.moviestore.dao.IWebsiteDao;
import net.yangziwen.moviestore.pojo.Website;
import net.yangziwen.moviestore.service.IWebsiteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

@Service
public class WebsiteServiceImpl implements IWebsiteService {
	
	@Autowired
	private IWebsiteDao websiteDao;

	@Override
	public Website getWebsiteById(Long id) {
		return websiteDao.getById(id);
	}
	
	@Override
	public Website getWebsiteByName(String name) {
		return websiteDao.first(new ModelMap().addAttribute("name", name));
	}
	
	@Override
	public List<Website> getWebsiteListResult(int start, int limit, Map<String, Object> param) {
		return websiteDao.list(start, limit, param);
	}
}

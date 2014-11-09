package net.yangziwen.moviestore.service.impl;

import java.util.List;
import java.util.Map;

import net.yangziwen.moviestore.dao.WebsiteJpaDao;
import net.yangziwen.moviestore.dao.base.DynamicSpecifications;
import net.yangziwen.moviestore.pojo.Website;
import net.yangziwen.moviestore.service.IWebsiteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebsiteServiceImpl implements IWebsiteService {
	
	@Autowired
	private WebsiteJpaDao websiteJpaDao;

	@Override
	public Website getWebsiteById(Long id) {
		return websiteJpaDao.findOne(id);
	}
	
	@Override
	public Website getWebsiteByName(String name) {
		return websiteJpaDao.getByName(name);
	}
	
	@Override
	public List<Website> getWebsiteListResult(int start, int limit, final Map<String, Object> param) {
		return websiteJpaDao.findAll(DynamicSpecifications.<Website>bySearchParam(param));
	}
	
}

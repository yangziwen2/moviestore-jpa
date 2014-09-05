package net.yangziwen.moviestore.service;

import java.util.List;
import java.util.Map;

import net.yangziwen.moviestore.pojo.Website;

public interface IWebsiteService {

	Website getWebsiteById(Long id);

	Website getWebsiteByName(String name);

	List<Website> getWebsiteListResult(int start, int limit, Map<String, Object> param);

}

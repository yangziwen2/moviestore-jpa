package net.yangziwen.moviestore.service.impl;

import java.util.List;
import java.util.Map;

import net.yangziwen.moviestore.dao.IMovieInfoDao;
import net.yangziwen.moviestore.dao.base.DaoConstant;
import net.yangziwen.moviestore.pojo.MovieInfo;
import net.yangziwen.moviestore.pojo.Website;
import net.yangziwen.moviestore.service.IMovieInfoService;
import net.yangziwen.moviestore.util.Page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

@Service
public class MovieInfoServiceImpl implements IMovieInfoService {
	
	@Autowired
	private IMovieInfoDao movieInfoDao;

	@Override
	public MovieInfo getMovieInfoById(Long id) {
		return movieInfoDao.getById(id);
	}
	
	@Override
	public void saveOrUpdateMovieInfo(MovieInfo movieInfo) {
		movieInfoDao.saveOrUpdate(movieInfo);
	}

	@Override
	public MovieInfo getMovieInfoWithMaxMovieId(Long websiteId) {
		return movieInfoDao.first(new ModelMap()
			.addAttribute("websiteId", websiteId)
			.addAttribute(DaoConstant.ORDER_BY, new ModelMap().addAttribute("movieId", DaoConstant.ORDER_DESC))
		);
	}
	
	@Override
	public Page<MovieInfo> getMovieInfoPaginateResult(int start, int limit, Map<String, Object> param) {
		return movieInfoDao.paginate(start, limit, param);
	}
	
	@Override
	public List<String> getMovieInfoYearListByWebsite(Website website) {
		return movieInfoDao.getYearListByWebsite(website);
	}
	
	@Override
	public List<String> getMovieInfoAreaListByWebsite(Website website) {
		return movieInfoDao.getAreaListByWebsite(website);
	}
	
	@Override
	public List<String> getMovieInfoCategoryListByWebsite(Website website) {
		return movieInfoDao.getCategoryListByWebsite(website);
	}
	
	@Override
	public int batchSaveMovieInfo(MovieInfo[] infos, int batchSize) {
		return movieInfoDao.batchSave(infos, batchSize);
	}
	
	@Override
	public List<String> getMovieInfoSubcategoryListByWebsiteAndCategory(Website website, String category) {
		return movieInfoDao.getSubcategoryListByWebsiteAndCategory(website, category);
	}
}

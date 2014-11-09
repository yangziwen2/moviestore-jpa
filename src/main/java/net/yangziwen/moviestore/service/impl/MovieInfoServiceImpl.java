package net.yangziwen.moviestore.service.impl;

import java.util.List;
import java.util.Map;

import net.yangziwen.moviestore.dao.MovieInfoJpaDao;
import net.yangziwen.moviestore.dao.base.DynamicSpecifications;
import net.yangziwen.moviestore.pojo.MovieInfo;
import net.yangziwen.moviestore.pojo.Website;
import net.yangziwen.moviestore.service.IMovieInfoService;
import net.yangziwen.moviestore.util.Page;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class MovieInfoServiceImpl implements IMovieInfoService {
	
	@Autowired
	private MovieInfoJpaDao movieInfoJpaDao;
	
	@Override
	public MovieInfo getMovieInfoById(Long id) {
		return movieInfoJpaDao.findOne(id);
	}
	
	@Override
	public void saveOrUpdateMovieInfo(MovieInfo movieInfo) {
		movieInfoJpaDao.save(movieInfo);
	}

	@Override
	public MovieInfo getMovieInfoWithMaxMovieId(Long websiteId) {
		List<MovieInfo> list = movieInfoJpaDao.findByWebsiteIdOrderByMovieIdDesc(websiteId, new PageRequest(0, 1));
		return CollectionUtils.isNotEmpty(list)? list.get(0): null;
	}
	
	@Override
	public Page<MovieInfo> getMovieInfoPaginateResult(int start, int limit, final Map<String, Object> param) {
		Specification<MovieInfo> specification = DynamicSpecifications.bySearchParam(param);
		org.springframework.data.domain.Page<MovieInfo> jpaPage = 
				movieInfoJpaDao.findAll(specification, new PageRequest(start / limit, limit));
		return Page.transform(jpaPage);
	}
	
	@Override
	public List<String> getMovieInfoYearListByWebsite(Website website) {
		return movieInfoJpaDao.getYearListByWebsiteId(website.getId());
	}
	
	@Override
	public List<String> getMovieInfoAreaListByWebsite(Website website) {
		return movieInfoJpaDao.getAreaListByWebsiteId(website.getId());
	}
	
	@Override
	public List<String> getMovieInfoCategoryListByWebsite(Website website) {
		return movieInfoJpaDao.getCategoryListByWebsiteId(website.getId());
	}
	
	@Override
	public int batchSaveMovieInfo(MovieInfo[] infos, int batchSize) {
		return movieInfoJpaDao.batchSave(infos, batchSize);
	}
	
	@Override
	public List<String> getMovieInfoSubcategoryListByWebsiteAndCategory(Website website, String category) {
		return movieInfoJpaDao.getSubcategoryListByWebsiteIdAndCategory(website.getId(), category);
	}
}

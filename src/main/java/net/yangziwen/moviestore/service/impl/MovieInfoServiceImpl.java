package net.yangziwen.moviestore.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.yangziwen.moviestore.dao.IMovieInfoDao;
import net.yangziwen.moviestore.dao.impl.jpa.IMovieInfoJpaDao;
import net.yangziwen.moviestore.pojo.MovieInfo;
import net.yangziwen.moviestore.pojo.Website;
import net.yangziwen.moviestore.service.IMovieInfoService;
import net.yangziwen.moviestore.util.Page;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.ejb.criteria.OrderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class MovieInfoServiceImpl implements IMovieInfoService {
	
	@Autowired
	private IMovieInfoDao movieInfoDao;
	
	@Autowired
	private IMovieInfoJpaDao movieInfoJpaDao;
	
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
		List<MovieInfo> list = movieInfoJpaDao.findByWebsiteIdOrderByMovieId(websiteId, new PageRequest(0, 1));
		return CollectionUtils.isNotEmpty(list)? list.get(0): null;
	}
	
	@Override
	public Page<MovieInfo> getMovieInfoPaginateResult(int start, int limit, final Map<String, Object> param) {
		org.springframework.data.domain.Page<MovieInfo> jpaPage =  movieInfoJpaDao.findAll(new Specification<MovieInfo>() {
			@Override
			public Predicate toPredicate(Root<MovieInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> restrictions = new ArrayList<Predicate>();
				for(Entry<String, Object> entry: param.entrySet()) {
					String key = entry.getKey();
					if(StringUtils.isBlank(key)) {
						continue;
					}
					if(key.startsWith("year")) {
						restrictions.add(cb.equal(root.get("year").as(String.class), entry.getValue()));
					} else if (key.startsWith("area")) {
						restrictions.add(cb.equal(root.get("area").as(String.class), entry.getValue()));
					} else if (key.startsWith("category")) {
						restrictions.add(cb.equal(root.get("category").as(String.class), entry.getValue()));
					} else if (key.startsWith("subcategory")) {
						restrictions.add(cb.equal(root.get("subcategory").as(String.class), entry.getValue()));
					} else if (key.startsWith("title")) {
						restrictions.add(cb.like(root.get("title").as(String.class), "%" + entry.getValue() + "%"));
					} else if (key.startsWith("actors")) {
						restrictions.add(cb.like(root.get("actors").as(String.class), "%" + entry.getValue() + "%"));
					} else if (key.startsWith("websiteId")) {
						restrictions.add(cb.equal(root.get("websiteId").as(Long.class), entry.getValue()));
					}
				}
				return query.where(restrictions.toArray(new Predicate[]{}))
					.orderBy(new OrderImpl(root.get("movieId"), false))
					.getRestriction();
			}
		}, new PageRequest(start / limit, limit));
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
		return movieInfoDao.batchSave(infos, batchSize);
	}
	
	@Override
	public List<String> getMovieInfoSubcategoryListByWebsiteAndCategory(Website website, String category) {
		return movieInfoJpaDao.getSubcategoryListByWebsiteIdAndCategory(website.getId(), category);
	}
}

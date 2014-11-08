package net.yangziwen.moviestore.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.yangziwen.moviestore.dao.MovieInfoJpaDao;
import net.yangziwen.moviestore.dao.base.DynamicSpecifications;
import net.yangziwen.moviestore.dao.base.DynamicSpecifications.ClauseEnum;
import net.yangziwen.moviestore.dao.base.clause.Clause;
import net.yangziwen.moviestore.dao.base.clause.ConditionClause;
import net.yangziwen.moviestore.dao.base.clause.OrderByClause;
import net.yangziwen.moviestore.dao.base.filter.AndFilter;
import net.yangziwen.moviestore.dao.base.filter.AndFilter.Operator;
import net.yangziwen.moviestore.dao.base.filter.OrderByFilter;
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
	
	@Deprecated
	public Page<MovieInfo> getMovieInfoPaginateResult1(int start, int limit, final Map<String, Object> param) {
		Map<ClauseEnum, Clause<MovieInfo>> clauseMap = new HashMap<ClauseEnum, Clause<MovieInfo>>();
		ConditionClause<MovieInfo> whereClause = new ConditionClause<MovieInfo>();
		for(Entry<String, Object> entry: param.entrySet()) {
			String[] arr = entry.getKey().split("__");
			if(arr.length < 2) {
				continue;
			}
			try {
				whereClause.and(new AndFilter(arr[0], Operator.valueOf(arr[1]), entry.getValue()));
			} catch (Exception e) {
			}
		}
		clauseMap.put(ClauseEnum.WHERE_CLAUSE, whereClause);
		
		OrderByClause<MovieInfo> orderByClause = new OrderByClause<MovieInfo>();
		orderByClause.order(new OrderByFilter("movieId", true));
		clauseMap.put(ClauseEnum.ORDER_BY_CLAUSE, orderByClause);
		
		Specification<MovieInfo> specification = DynamicSpecifications.bySearchFilter(clauseMap);
		
		org.springframework.data.domain.Page<MovieInfo> jpaPage =  movieInfoJpaDao.findAll(specification, new PageRequest(start / limit, limit));
		return Page.transform(jpaPage);
	}
	
	@Deprecated
	public Page<MovieInfo> getMovieInfoPaginateResult2(int start, int limit, final Map<String, Object> param) {
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
		return movieInfoJpaDao.batchSave(infos, batchSize);
	}
	
	@Override
	public List<String> getMovieInfoSubcategoryListByWebsiteAndCategory(Website website, String category) {
		return movieInfoJpaDao.getSubcategoryListByWebsiteIdAndCategory(website.getId(), category);
	}
}

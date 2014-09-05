package net.yangziwen.moviestore.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.yangziwen.moviestore.dao.IWebsiteDao;
import net.yangziwen.moviestore.dao.base.DaoConstant;
import net.yangziwen.moviestore.dao.impl.jpa.IWebsiteJpaDao;
import net.yangziwen.moviestore.pojo.Website;
import net.yangziwen.moviestore.service.IWebsiteService;

import org.hibernate.ejb.criteria.OrderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class WebsiteServiceImpl implements IWebsiteService {
	
	@Autowired
	private IWebsiteDao websiteDao;
	
	@Autowired
	private IWebsiteJpaDao websiteJpaDao;

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
		
		return websiteJpaDao.findAll(new Specification<Website>() {
			@Override
			public Predicate toPredicate(Root<Website> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				Object orderBy = param.get(DaoConstant.ORDER_BY);
				if(orderBy instanceof Map) {
					List<Order> orderList = new ArrayList<Order>();
					Map<String, Object> orderByMap = (Map<String, Object>) orderBy;
					for(Entry<String, Object> entry: orderByMap.entrySet()) {
						orderList.add(new OrderImpl(root.get(entry.getKey()), DaoConstant.ORDER_ASC.equals(entry.getValue())));
					}
					return query.orderBy(orderList).getRestriction();
				}
				return builder.conjunction();
			}
		});
		
//		return websiteDao.list(start, limit, param);
	}
	
	
}

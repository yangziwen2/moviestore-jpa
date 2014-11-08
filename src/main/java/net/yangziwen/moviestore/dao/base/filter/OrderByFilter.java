package net.yangziwen.moviestore.dao.base.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

import net.yangziwen.moviestore.dao.base.DynamicSpecifications;

import org.hibernate.ejb.criteria.OrderImpl;

public class OrderByFilter {

	public String fieldName;
	public boolean reverse;
	
	public OrderByFilter(String fieldName, boolean reverse) {
		this.fieldName = fieldName;
		this.reverse = reverse;
	}
	
	public <T> OrderImpl toOrderImpl(Root<T> root, CriteriaBuilder builder) {
		return new OrderImpl(DynamicSpecifications.getFieldPath(fieldName, root), !reverse);
	}
	
}

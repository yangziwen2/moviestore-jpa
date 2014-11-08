package net.yangziwen.moviestore.dao.base.clause;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import net.yangziwen.moviestore.dao.base.filter.OrderByFilter;

public class OrderByClause<T> implements Clause<T> {

	private List<OrderByFilter> orderList = new ArrayList<OrderByFilter>();
	
	public OrderByClause<T> order(OrderByFilter orderByFilter) {
		orderList.add(orderByFilter);
		return this;
	}
	
	public OrderByFilter getOrderByFilter(int index) {
		if(index < 0 || index >= sizeOfOrderByFilters()) {
			throw new IndexOutOfBoundsException(String.format("index[%d] of orderByFilter doesn't exist!", index));
		}
		return orderList.get(index);
	}
	
	public int sizeOfOrderByFilters() {
		return orderList.size();
	}
	
	public static <T> OrderByClause<T> order(OrderByFilter... orderByFilters) {
		OrderByClause<T> clause = new OrderByClause<T>();
		if(orderByFilters == null) {
			return clause;
		}
		clause.orderList.addAll(Arrays.asList(orderByFilters));
		return clause;
	}

	@Override
	public void fillToQuery(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		List<Order> orders = new ArrayList<Order>();
		for(OrderByFilter orderBy: orderList) {
			if(orderBy == null) {
				continue;
			}
			orders.add(orderBy.toOrderImpl(root, builder));
		}
		query.orderBy(orders);
	}
	
	
}

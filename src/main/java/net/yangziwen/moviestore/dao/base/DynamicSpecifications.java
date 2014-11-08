package net.yangziwen.moviestore.dao.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.yangziwen.moviestore.dao.base.clause.Clause;
import net.yangziwen.moviestore.dao.base.clause.ConditionClause;
import net.yangziwen.moviestore.dao.base.clause.OrderByClause;
import net.yangziwen.moviestore.dao.base.filter.AndFilter;
import net.yangziwen.moviestore.dao.base.filter.AndFilter.Operator;
import net.yangziwen.moviestore.dao.base.filter.OrFilter;
import net.yangziwen.moviestore.dao.base.filter.OrderByFilter;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class DynamicSpecifications {

	public static <T> Specification<T> bySearchFilter(final Map<ClauseEnum, Clause<T>> clauseMap) {
		return new Specification<T>() {
			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				
				ConditionClause<T> whereClause = ClauseEnum.WHERE_CLAUSE.<T, ConditionClause<T>>fetchFromMap(clauseMap);
				if(whereClause != null) {
					whereClause.fillToQuery(root, query, builder);
				}
				
				OrderByClause<T> orderByClause = ClauseEnum.ORDER_BY_CLAUSE.<T, OrderByClause<T>>fetchFromMap(clauseMap);
				if(orderByClause != null) {
					orderByClause.fillToQuery(root, query, builder);
				}
				
				return query.getRestriction();
			}
		};
	}
	
	public static <T> Specification<T> bySearchParam(final Map<String, Object> params) {
		final OrderByClause<T> orderByClause = buildOrderByClauseByParams(params);
		final ConditionClause<T> whereClause = buildWhereClauseByParams(params);
		return new Specification<T>() {
			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				whereClause.fillToQuery(root, query, builder);
				orderByClause.fillToQuery(root, query, builder);
				return query.getRestriction();
			}
		};
	}
	
	private static <T> ConditionClause<T> buildWhereClauseByParams(Map<String, Object> params) {
		ConditionClause<T> whereClause = new ConditionClause<T>();
		for(Entry<String, Object> entry: params.entrySet()) {
			if(entry.getKey().contains(DaoConstant.ORDER_BY)) {
				whereClause.or(buildOrFilter((Map<String, Object>)entry.getValue()));
			}
			AndFilter andFilter = buildAndFilter(entry.getKey(), entry.getValue());
			if(andFilter == null) {
				continue;
			}
			whereClause.and(andFilter);
		}
		return whereClause;
	}
	
	private static OrFilter buildOrFilter(Map<String, Object> orParams) {
		List<AndFilter> andFilters = new ArrayList<AndFilter>();
		for(Entry<String, Object> entry: orParams.entrySet()) {
			AndFilter andFilter = buildAndFilter(entry.getKey(), entry.getValue());
			if(andFilter == null) {
				continue;
			}
			andFilters.add(andFilter);
		}
		return OrFilter.and(andFilters.toArray(new AndFilter[]{}));
	}
	
	private static AndFilter buildAndFilter(String key, Object value) {
		String[] splitedKey = key.split("__");
		if(splitedKey.length < 2) {
			return new AndFilter(key, value);
		} else {
			return new AndFilter(splitedKey[0], Operator.valueOf(splitedKey[1]), value);
		}
	}
	
	private static <T> OrderByClause<T> buildOrderByClauseByParams(Map<String, Object> params) {
		Map<String, Object> orderByParams = (Map<String, Object>) params.remove(DaoConstant.ORDER_BY);
		if(MapUtils.isEmpty(orderByParams)) {
			return null;
		}
		OrderByClause<T> clause = new OrderByClause<T>();
		for(Entry<String, Object> entry: orderByParams.entrySet()) {
			clause.order(new OrderByFilter(entry.getKey(), DaoConstant.ORDER_DESC.equals(entry.getValue())));
		}
		return clause;
	}
	
	@SuppressWarnings("unchecked")
	public static <T, Y> Path<Y> getFieldPath(String fieldName, Root<T> root) {
		String[] names = StringUtils.split(fieldName, ".");
		Path<?> fieldPath = root.get(names[0]);
		for(int i=1; i<names.length; i++) {
			fieldPath = fieldPath.get(names[i]);
		}
		return (Path<Y>) fieldPath;
	}
	
	
	public enum ClauseEnum {
		
		WHERE_CLAUSE, 
		ORDER_BY_CLAUSE;
		
		@SuppressWarnings("unchecked")
		public <T, C> C fetchFromMap(Map<ClauseEnum, Clause<T>> clauseMap) {
			Clause<T> clause = clauseMap.get(this);
			if(clause == null) {
				return null;
			}
			return (C) clause;
		}
		
	}
}

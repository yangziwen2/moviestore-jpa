package net.yangziwen.moviestore.dao.base.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.yangziwen.moviestore.dao.base.DynamicSpecifications;
import net.yangziwen.moviestore.dao.base.PredicateConverter;

import org.apache.commons.lang3.ArrayUtils;

public class AndFilter implements PredicateConverter {
	
	public String fieldName;
	public Object value;
	public Operator operator = Operator.eq;
	
	public AndFilter(String fieldName, Operator operator, Object value) {
		this.fieldName = fieldName;
		this.operator = operator;
		this.value = value;
	}
	
	public AndFilter(String fieldName, Object value) {
		this(fieldName, Operator.eq, value);
	}
	
	public <T> Predicate toPredicate(Root<T> root, CriteriaBuilder builder) {
		return operator.toPredicate(root, builder, this);
	}
	
	public static enum Operator {
		eq {
			@Override
			protected <T> Predicate toPredicate(Root<T> root, CriteriaBuilder builder, AndFilter andFilter) {
				return builder.equal(DynamicSpecifications.getFieldPath(andFilter.fieldName, root), andFilter.value);
			}
		}, ne {
			@Override
			protected <T> Predicate toPredicate(Root<T> root, CriteriaBuilder builder, AndFilter andFilter) {
				return builder.notEqual(DynamicSpecifications.getFieldPath(andFilter.fieldName, root), andFilter.value);
			}
		}, gt {
			@Override
			@SuppressWarnings({ "unchecked", "rawtypes" })
			protected <T> Predicate toPredicate(Root<T> root, CriteriaBuilder builder, AndFilter andFilter) {
				return builder.greaterThan(DynamicSpecifications.<T, Comparable>getFieldPath(andFilter.fieldName, root), (Comparable)andFilter.value);
			}
		}, ge {
			@Override
			@SuppressWarnings({ "unchecked", "rawtypes" })
			protected <T> Predicate toPredicate(Root<T> root, CriteriaBuilder builder, AndFilter andFilter) {
				return builder.greaterThanOrEqualTo(DynamicSpecifications.<T, Comparable>getFieldPath(andFilter.fieldName, root), (Comparable)andFilter.value);
			}
		}, lt {
			@Override
			@SuppressWarnings({ "unchecked", "rawtypes" })
			protected <T> Predicate toPredicate(Root<T> root, CriteriaBuilder builder, AndFilter andFilter) {
				return builder.lessThan(DynamicSpecifications.<T, Comparable>getFieldPath(andFilter.fieldName, root), (Comparable)andFilter.value);
			}
		}, le {
			@Override
			@SuppressWarnings({ "unchecked", "rawtypes" })
			protected <T> Predicate toPredicate(Root<T> root, CriteriaBuilder builder, AndFilter andFilter) {
				return builder.lessThanOrEqualTo(DynamicSpecifications.<T, Comparable>getFieldPath(andFilter.fieldName, root), (Comparable)andFilter.value);
			}
		}, contain {
			@Override
			protected <T> Predicate toPredicate(Root<T> root, CriteriaBuilder builder, AndFilter andFilter) {
				return builder.like(DynamicSpecifications.<T, String>getFieldPath(andFilter.fieldName, root), "%" + andFilter.value + "%");
			}
		}, not_contain {
			@Override
			protected <T> Predicate toPredicate(Root<T> root, CriteriaBuilder builder, AndFilter andFilter) {
				return builder.notLike(DynamicSpecifications.<T, String>getFieldPath(andFilter.fieldName, root), "%" + andFilter.value + "%");
			}
		}, start_with {
			@Override
			protected <T> Predicate toPredicate(Root<T> root, CriteriaBuilder builder, AndFilter andFilter) {
				return builder.like(DynamicSpecifications.<T, String>getFieldPath(andFilter.fieldName, root), andFilter.value + "%");
			}
		}, not_start_with {
			@Override
			protected <T> Predicate toPredicate(Root<T> root, CriteriaBuilder builder, AndFilter andFilter) {
				return builder.notLike(DynamicSpecifications.<T, String>getFieldPath(andFilter.fieldName, root), andFilter.value + "%");
			}
		}, end_with {
			@Override
			protected <T> Predicate toPredicate(Root<T> root, CriteriaBuilder builder, AndFilter andFilter) {
				return builder.like(DynamicSpecifications.<T, String>getFieldPath(andFilter.fieldName, root), "%" + andFilter.value);
			}
		}, not_end_with {
			@Override
			protected <T> Predicate toPredicate(Root<T> root, CriteriaBuilder builder, AndFilter andFilter) {
				return builder.notLike(DynamicSpecifications.<T, String>getFieldPath(andFilter.fieldName, root), "%" + andFilter.value);
			}
		}, in {
			@Override
			protected <T> Predicate toPredicate(Root<T> root, CriteriaBuilder builder, AndFilter andFilter) {
				In<Object> in = builder.in(DynamicSpecifications.getFieldPath(andFilter.fieldName, root));
				Object value = andFilter.value;
				if(Iterable.class.isInstance(value)){
					for(Object obj: Iterable.class.cast(andFilter.value)) {
						in.value(obj);
					}
				} else if (value.getClass().isArray()) {
					for(Object obj: toObjectArray(value)) {
						in.value(obj);
					}
				}
				return in;
			}
		}, not_in {
			@Override
			protected <T> Predicate toPredicate(Root<T> root, CriteriaBuilder builder, AndFilter andFilter) {
				return builder.not(in.toPredicate(root, builder, andFilter));
			}
		}, is_null {
			@Override
			protected <T> Predicate toPredicate(Root<T> root, CriteriaBuilder builder, AndFilter andFilter) {
				return builder.isNull(DynamicSpecifications.getFieldPath(andFilter.fieldName, root));
			}
		};
		
		protected abstract <T> Predicate toPredicate(Root<T> root, CriteriaBuilder builder, AndFilter andFilter);
		
		private static Object[] toObjectArray(Object array) {
			if(array == null) {
				return ArrayUtils.EMPTY_OBJECT_ARRAY;
			}
			if(array instanceof Object[]) {
				return (Object[]) array;
			}
			Class<?> clazz = array.getClass();
			if(clazz == short[].class) {
				return ArrayUtils.toObject((short[]) array);
			} else if(clazz == int[].class) {
				return ArrayUtils.toObject((int[]) array);
			} else if(clazz == long[].class) {
				return ArrayUtils.toObject((long[]) array);
			} else if(clazz == float[].class) {
				return ArrayUtils.toObject((float[]) array);
			} else if(clazz == double[].class) {
				return ArrayUtils.toObject((double[]) array);
			} else if(clazz == char[].class) {
				return ArrayUtils.toObject((char[]) array);
			} else if(clazz == byte[].class) {
				return ArrayUtils.toObject((byte[]) array);
			} else if(clazz == boolean[].class) {
				return ArrayUtils.toObject((boolean[]) array);
			}
			return ArrayUtils.EMPTY_OBJECT_ARRAY;
		}
		
	}

}

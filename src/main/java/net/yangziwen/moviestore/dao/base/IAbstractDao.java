package net.yangziwen.moviestore.dao.base;

import java.util.List;
import java.util.Map;

import net.yangziwen.moviestore.pojo.AbstractModel;
import net.yangziwen.moviestore.util.Page;

public interface IAbstractDao<E extends AbstractModel> {

	void saveOrUpdate(E entity);

	void delete(E entity);

	E getById(Long id);

	List<E> list(int start, int limit, Map<String, Object> param);

	List<E> list(Map<String, Object> param);
	
	int count(Map<String, Object> param);

	Page<E> paginate(int start, int limit, Map<String, Object> param);

	void deleteById(Long id);

	E first(Map<String, Object> param);

	E unique(Map<String, Object> param);

	int executeSql(String sql);

	int executeSql(String sql, Map<String, Object> param);

	void flush();

	void refresh(E entity);

}

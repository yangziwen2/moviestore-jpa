package net.yangziwen.moviestore.dao.custom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.yangziwen.moviestore.pojo.MovieInfo;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Repository;

/**
 * The name of the Impl class must be consistent with the DAO interface, 
 * no matter what the customized interface is named.
 * This convention is really obscure!
 */
@Repository
public class MovieInfoJpaDaoImpl implements MovieInfoJpaCustomRepository {
	
	@PersistenceContext
	private EntityManager entityManager;

	public int batchSave(MovieInfo[] infos, int batchSize) {
		if(ArrayUtils.isEmpty(infos)) {
			return 0;
		}
		for(int i = 0, l = infos.length; i < l; i += batchSize) {
			int bound = Math.min(l - i, batchSize);
			for(int j = 0; j < bound; j++) {
				entityManager.persist(infos[i + j]);
			}
			entityManager.flush();
			entityManager.clear();
		}
		return infos.length;
	}
}

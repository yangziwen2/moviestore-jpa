package net.yangziwen.moviestore.dao.custom;

import net.yangziwen.moviestore.pojo.MovieInfo;

public interface MovieInfoJpaCustomRepository {

	public int batchSave(MovieInfo[] infos, int batchSize);
	
}

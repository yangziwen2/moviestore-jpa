package net.yangziwen.moviestore.dao.impl.jpa;

import net.yangziwen.moviestore.pojo.MovieInfo;

public interface MovieInfoRepositoryCustom {

	public int batchSave(MovieInfo[] infos, int batchSize);
	
}

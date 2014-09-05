package net.yangziwen.moviestore.dao.impl.jpa;

import net.yangziwen.moviestore.pojo.MovieInfo;
import net.yangziwen.moviestore.pojo.Website;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IMovieInfoJpaDao extends JpaRepository<MovieInfo, Long>, JpaSpecificationExecutor<Website>{

}

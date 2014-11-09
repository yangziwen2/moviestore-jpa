package net.yangziwen.moviestore.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.alibaba.fastjson.annotation.JSONField;

@Entity
@Table(name="movie_info")
@XmlRootElement
public class MovieInfo extends AbstractModel {
	
	public static final MovieInfo INVALID_INFO = new MovieInfo();
	
	public static final int MAX_ACTORS_LENGTH = 200;

	@Id
	@GeneratedValue
	@Column
	@JSONField(serialize = false)
	private Long id;
	
	@Column(name="website_id")
	@JSONField(serialize = false)
	private Long websiteId;
	
	/**
	 * 对应相应网站的movie的id，
	 * 用于替换Website.movieUrlTemplate中的占位符
	 */
	@Column(name="movie_id")
	private Long movieId;
	
	@Column
	private String title;
	
	private String actors;
	
	@Column
	private String area;
	
	@Column
	private String year;
	
	@Column
	private String category;
	
	@Column
	private String subcategory;
	
	@Column(name="photo_url")
	private String photoUrl;
	
	public MovieInfo() {}
	
	@XmlAttribute
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getWebsiteId() {
		return websiteId;
	}
	public void setWebsiteId(Long websiteId) {
		this.websiteId = websiteId;
	}
	public Long getMovieId() {
		return movieId;
	}
	public void setMovieId(Long movieId) {
		this.movieId = movieId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getActors() {
		return actors;
	}
	public void setActors(String actors) {
		this.actors = actors;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getPhotoUrl() {
		return photoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSubcategory() {
		return subcategory;
	}
	public void setSubcategory(String subcategory) {
		this.subcategory = subcategory;
	}
	
}

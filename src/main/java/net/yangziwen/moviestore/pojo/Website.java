package net.yangziwen.moviestore.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="website")
public class Website extends AbstractModel {
	
	private static final String MOVIE_ID_PLACEHOLDER = "${movieId}";

	@Id
	@GeneratedValue
	@Column
	private Long id;
	
	@Column
	private String name;
	
	@Column(name="display_name")
	private String displayName;
	
	@Column(name="home_page_url")
	private String homePageUrl;
	
	@Column(name="movie_url_template")
	private String movieUrlTemplate;
	
	@Column(name="test_proxy_url")
	private String testProxyUrl;
	
	@Column(name="mock_photo_referer")
	private Boolean mockPhotoReferer;
	
	@Column
	private Integer rank;
	
	public Website() {}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getHomePageUrl() {
		return homePageUrl;
	}
	public void setHomePageUrl(String homePageUrl) {
		this.homePageUrl = homePageUrl;
	}

	public String getMovieUrlTemplate() {
		return movieUrlTemplate;
	}
	public void setMovieUrlTemplate(String movieUrlTemplate) {
		this.movieUrlTemplate = movieUrlTemplate;
	}
	public String getTestProxyUrl() {
		return testProxyUrl;
	}
	public void setTestProxyUrl(String testProxyUrl) {
		this.testProxyUrl = testProxyUrl;
	}
	public Boolean getMockPhotoReferer() {
		return mockPhotoReferer;
	}
	public void setMockPhotoReferer(Boolean mockPhotoReferer) {
		this.mockPhotoReferer = mockPhotoReferer;
	}
	public Integer getRank() {
		return rank;
	}
	public void setRank(Integer rank) {
		this.rank = rank;
	}
	
	public String getMovieUrl(Object movieId) {
		return movieUrlTemplate.replace(MOVIE_ID_PLACEHOLDER, String.valueOf(movieId));
	}
	
}

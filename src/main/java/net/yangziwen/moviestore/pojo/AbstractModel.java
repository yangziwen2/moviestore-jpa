package net.yangziwen.moviestore.pojo;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


public abstract class AbstractModel {

	public abstract Long getId();
	
	public abstract void setId(Long id);
	
	public String toString() {
		return toString(ToStringStyle.MULTI_LINE_STYLE);
	}
	
	public String toString(ToStringStyle style) {
		return ToStringBuilder.reflectionToString(this, style);
	}
	
}

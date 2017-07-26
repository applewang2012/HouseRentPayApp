package tenant.guardts.house.model;

public class SurroundInfo {
	private String imageUrl;
	private String name;
	private String address;
	private String distance;
	private String star;
	private String uid;
	
	
	public String getPosterUrl() {
		return imageUrl;
	}

	public void setPosterUrl(String url) {
		this.imageUrl = url;
	}
	
	public String getNearName() {
		return name;
	}

	public void setNearName(String name) {
		this.name = name;
	}
	
	public String getNearAddress() {
		return address;
	}

	public void setNearAddress(String add) {
		this.address = add;
	}
	
	public String getNearDistance() {
		return distance;
	}

	public void setNearDistance(String dis) {
		this.distance = dis;
	}
	
	public String getNearStar() {
		return star;
	}

	public void setNearStar(String star) {
		this.star = star;
	}
	
	public String getNearUid() {
		return uid;
	}

	public void setNearUid(String uid) {
		this.uid = uid;
	}

}

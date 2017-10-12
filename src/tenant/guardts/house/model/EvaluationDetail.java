package tenant.guardts.house.model;

public class EvaluationDetail {
	private String user;
	private String date;
	private String serviceRating;
	private String environmentalRating;
	private String priceRating;
	public EvaluationDetail(String user, String date, String serviceRating, String environmentalRating,
			String priceRating) {
		super();
		this.user = user;
		this.date = date;
		this.serviceRating = serviceRating;
		this.environmentalRating = environmentalRating;
		this.priceRating = priceRating;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getServiceRating() {
		return serviceRating;
	}
	public void setServiceRating(String serviceRating) {
		this.serviceRating = serviceRating;
	}
	public String getEnvironmentalRating() {
		return environmentalRating;
	}
	public void setEnvironmentalRating(String environmentalRating) {
		this.environmentalRating = environmentalRating;
	}
	public String getPriceRating() {
		return priceRating;
	}
	public void setPriceRating(String priceRating) {
		this.priceRating = priceRating;
	}
	
	
}

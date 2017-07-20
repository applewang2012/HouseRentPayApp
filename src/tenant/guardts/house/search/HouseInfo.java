package tenant.guardts.house.search;

public class HouseInfo {

	private String id;
	private String type, rentType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHouseType() {
		return type;
	}

	public void setHouseType(String type) {
		this.type = type;
	}
	
	public String getRentType() {
		return rentType;
	}

	public void setRentType(String type) {
		this.rentType = type;
	}
	
}

package tenant.guardts.house.model;

public class HouseInfoModel {
	private String houseAddress;
	private String houseType;
	private String phone;
	private String area;
	private String startTime;
	private String direction;
	private String endTime;
	private boolean houseAvailable;
	private String houseId;
	private String ownerName;
	private String ownerIdcard;
	private String houseTotalFloor;
	private String houseStatus;
	private String houseCurrentFloor;
	
	public String getHouseId() {
		return houseId;
	}

	public void setHouseId(String id) {
		this.houseId = id;
	}

	public String getHouseAddress() {
		return houseAddress;
	}

	public void setHouseAddress(String address) {
		this.houseAddress = address;
	}

	public String getHouseType() {
		return houseType;
	}

	public void setHouseType(String type) {
		this.houseType = type;
	}

	public String getHouseEndTime() {
		return endTime;
	}

	public void setHouseEndTime(String time) {
		this.endTime = time;
	}
	
	public String getHousePhone() {
		return phone;
	}

	public void setHousePhone(String phone) {
		this.phone = phone;
	}
	
	public String getHouseArea() {
		return area;
	}

	public void setHouseArea(String area) {
		this.area = area;
	}
	
	public boolean getHouseAvailable() {
		return houseAvailable;
	}

	public void setHouseAvailable(boolean avilable) {
		this.houseAvailable = avilable;
	}
	
	public String getHouseOwnerName() {
		return ownerName;
	}

	public void setHouseOwnerName(String name) {
		this.ownerName = name;
	}
	
	public String getHouseOwnerIdcard() {
		return ownerIdcard;
	}

	public void setHouseOwnerIdcard(String id) {
		this.ownerIdcard = id;
	}
	
	public String getHouseStartTime() {
		return startTime;
	}

	public void setHouseStartTime(String time) {
		this.startTime = time;
	}
	
	public void setHouseDirection(String direction) {
		this.direction = direction;
	}

	public String getHouseDirection() {
		return direction;
	}
	
	public void setHouseTotalFloor(String floor) {
		this.houseTotalFloor = floor;
	}
	
	public String  getHouseTotalFloor() {
		return this.houseTotalFloor;
	}
	
	public String getHouseCurrentFloor() {
		return houseCurrentFloor;
	}

	public void setHouseCurrentFloor(String floor) {
		this.houseCurrentFloor = floor;
	}
	
	
	public String getHouseStatus() {
		return houseStatus;
	}

	public void setHouseStatus(String status) {
		this.houseStatus = status;
	}

}

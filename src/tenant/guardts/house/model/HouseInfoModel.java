package tenant.guardts.house.model;

import java.io.Serializable;

public class HouseInfoModel implements Serializable{
	private static final long serialVersionUID = -7060210544600464481L;
	private String houseAddress;
	private String houseType;
	private String ownerPhone;
	private String area;
	private String startTime;
	private long startTimeStamp, endTimeStamp;
	private String direction;
	private String endTime;
	private boolean houseAvailable;
	private String houseId;
	private String ownerName;
	private String ownerIdcard;//房主身份证
	private String renterIdcard;//租客身份证
	private String houseTotalFloor;
	private String houseStatus;
	private String houseCurrentFloor;
	private String price;
	private String houseOrderId;//订单号
	private String contactPhone;
	private String contactName;
	private String doorPassword;
	private String orderCreateDate;
	private long orderCreatedDateStamp;//下单时间
	private long orderExpiredDate;
	private long currentDate;  //当前时间
	private String mShowTimeDown;
	private String checkOutPerson;//退房申请人
	private String orderCreatedBy;
	

	public String getCheckOutPerson() {
		return checkOutPerson;
	}

	public void setCheckOutPerson(String checkOutPerson) {
		this.checkOutPerson = checkOutPerson;
	}

	public String getRenterIdcard() {
		return renterIdcard;
	}

	public String getOrderCreatedDate() {
		return orderCreateDate;
	}

	public void setOrderCreatedDate(String orderCreatedDate) {
		this.orderCreateDate = orderCreatedDate;
	}

	public long getOrderCreatedDateStamp() {
		return orderCreatedDateStamp;
	}

	public void setOrderCreatedDateStamp(long orderCreatedDate) {
		this.orderCreatedDateStamp = orderCreatedDate;
	}
	
	public long getOrderExpiredDate() {
		return orderExpiredDate;
	}

	public void setOrderExpiredDate(long expiredDate) {
		this.orderExpiredDate = expiredDate;
	}
	
	public long getCurrentdDateStamp() {
		return currentDate;
	}

	public void setCurrentDateStamp(long currentDate) {
		this.currentDate = currentDate;
	}
	
	public String getShowTimeDownTime() {
		return mShowTimeDown;
	}

	public void setShowTimeDownTime(String time) {
		this.mShowTimeDown = time;
	}

	public void setRenterIdcard(String renterIdcard) {
		this.renterIdcard = renterIdcard;
	}

	public String getHouseId() {
		return houseId;
	}

	public void setHouseId(String id) {
		this.houseId = id;
	}
	
	public String getHouseOrderId() {
		return houseOrderId;
	}

	public void setHouseOrderId(String id) {
		this.houseOrderId = id;
	}

	public String getHouseAddress() {
		return houseAddress;
	}

	public void setHouseAddress(String address) {
		this.houseAddress = address;
	}
	
	public String getHouseDirection() {
		return direction;
	}

	public void setHouseDirection(String address) {
		this.direction = address;
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
	
	public String getHouseOwnerPhone() {
		return ownerPhone;
	}

	public void setHouseOwnerPhone(String phone) {
		this.ownerPhone = phone;
	}
	
	public String getHouseContactPhone() {
		return contactPhone;
	}

	public void setHouseContactPhone(String phone) {
		this.contactPhone = phone;
	}
	
	public String getHouseContactName() {
		return contactName;
	}

	public void setHouseContactName(String name) {
		this.contactName = name;
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

	public void setHouseStartTimeStamp(long time) {
		this.endTimeStamp = time;
	}
	
	public long getHouseStartTimeStamp() {
		return endTimeStamp;
	}

	public void setHouseEndTimeStamp(long time) {
		this.startTimeStamp = time;
	}
	
	public long getHouseEndTimeStamp() {
		return startTimeStamp;
	}
	
	public void setHouseStartTime(String time) {
		this.startTime = time;
	}
	
	public void setHousePrice(String direction) {
		this.price = direction;
	}

	public String getHousePrice() {
		return price;
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
	
	public String getDoorPassword() {
		return doorPassword;
	}

	public void setDoorPassword(String password) {
		this.doorPassword = password;
	}
	
	public String getOrderCreatedBy() {
		return this.orderCreatedBy;
	}

	public void setOrderCreatedBy(String create) {
		this.orderCreatedBy = create;
	}

}

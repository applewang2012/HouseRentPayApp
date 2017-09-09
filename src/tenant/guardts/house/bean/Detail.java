package tenant.guardts.house.bean;

import com.google.gson.annotations.SerializedName;

public class Detail {
	@SerializedName("Fee")
	public String fee;
	@SerializedName("Memo")
	public String memo;
	@SerializedName("BillDesc")
	public String billDesc;
	@SerializedName("PayType")
	public String payType;
	@SerializedName("PayDate")
	public String payDate;
	
}

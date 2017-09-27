package tenant.guardts.house.bean;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Detail implements Serializable{
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

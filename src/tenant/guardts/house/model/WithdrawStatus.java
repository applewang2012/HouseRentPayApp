package tenant.guardts.house.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class WithdrawStatus implements Serializable{
	@SerializedName("ret_code")
	public String retCode;
	@SerializedName("ret_msg")
	public String retMsg;

}

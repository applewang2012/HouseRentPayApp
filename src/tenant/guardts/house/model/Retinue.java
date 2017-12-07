package tenant.guardts.house.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Retinue implements Serializable{
	
	@SerializedName(value = "PartnerName")
	public String name;
	@SerializedName(value = "PartnerIdCard")
	public String idcard;
	public Retinue(String name, String idcard) {
		super();
		this.name = name;
		this.idcard = idcard;
	}
	
	

}

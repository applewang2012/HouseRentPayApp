package tenant.guardts.house.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class RetinuesResult implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	@SerializedName(value = "Name")
	public String name;
	@SerializedName(value="IDCard")
	public String idcard;
	public RetinuesResult(String name, String idcard) {
		super();
		this.name = name;
		this.idcard = idcard;
	}
	@Override
	public String toString() {
		return name+"--"+idcard;
	}
	
	
	
}

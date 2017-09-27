package tenant.guardts.house.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class RentalDetail implements Serializable{
	@SerializedName("RentRecord")
	public String rentRecord;
	public String ret;
}

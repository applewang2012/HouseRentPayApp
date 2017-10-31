package tenant.guardts.house.model;

public class HouseSelectorModel {
	
	private String[] allValue;
	private String selectValue;
	private String orginText;
	private String[] allValueId;
	private String[] allLinkName;
	private String selectId;
	private int position;

	public String getHouseSelectValue() {
		return selectValue;
	}

	public void setHouseSelectValue(String val) {
		this.selectValue = val;
	}
	
	public String getHouseSelectId() {
		return selectId;
	}

	public void setHouseSelectId(String id) {
		this.selectId = id;
	}
	
	public int getHouseSelectPosition() {
		return position;
	}

	public void setHouseSelectPosition(int position) {
		this.position = position;
	}

	public String getHouseOrginText() {
		return orginText;
	}

	public void setHouseOrginText(String text) {
		this.orginText = "";
	}
	
	public String [] getHouseAllContent() {
		return allValue;
	}

	public void setHouseAllContent(String [] value) {
		this.allValue = value;
	}
	
	public String [] getHouseAllId() {
		return allValueId;
	}

	public void setHouseAllId(String [] value) {
		this.allValueId = value;
	}
	
	public String [] getHouseAllLinkName() {
		return allLinkName;
	}

	public void setHouseAllLinkName(String [] value) {
		this.allLinkName = value;
	}
	
}

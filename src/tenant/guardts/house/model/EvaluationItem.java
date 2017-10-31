package tenant.guardts.house.model;

import java.io.Serializable;

public class EvaluationItem implements Serializable,Comparable<EvaluationItem>{
	public String EvaluatePerson;
	public String EvaluateItem0;
	public String EvaluateItem1;
	public String EvaluateItem2;
	public String EvaluateDate;
	public String EvaluateObject;
	@Override
	public int compareTo(EvaluationItem another) {
		if(getNum(this.EvaluateDate).compareTo(getNum(another.EvaluateDate))>0){
			return -1;
		}
		if(getNum(this.EvaluateDate).compareTo(getNum(another.EvaluateDate))<0){
			return 1;
		}
		return 0;
	}
	/**去掉Date()
	 * @param EvaluateDate Date(1508923948)
	 * @return 1508923948
	 */
	private String getNum(String EvaluateDate){
		return EvaluateDate.substring(6,
				EvaluateDate.length() - 2);
		 
	}
	
	
}

import java.util.ArrayList;

public class CPMark {
	Double headTime = null;
	Double tailTime = null;
	ArrayList<Double> headCurr = null;
	ArrayList<Double> tailCurr = null;
	Double nearPoti = null;
	
	public CPMark() {
		headCurr = new ArrayList<>();
		tailCurr = new ArrayList<>();
	}
	public boolean ready() {
		return (nearPoti == null)? false : true;
	}
	
	public Double getHeadTime() {
		return headTime;
	}

	public Double getTailTime() {
		return tailTime;
	}
	public Integer getExperimentCount(){
		return headCurr.size();
	}
	public Double getHeadCurr(Integer which) {
		return headCurr.get(which);
	}

	public Double getTailCurr(Integer which) {
		return tailCurr.get(which);
	}

	public Double getNearPoti() {
		return nearPoti;
	}

	public void setHeadTime(Double headTime) {
		this.headTime = headTime;
	}

	public void setTailTime(Double tailTime) {
		this.tailTime = tailTime;
	}

	public void setHeadCurr(Double headCurr) {
		this.headCurr.add(headCurr);
	}

	public void setTailCurr(Double tailCurr) {
		this.tailCurr.add(tailCurr);
	}

	public void setNearPoti(Double nearPoti) {
		this.nearPoti = nearPoti;
	}

	@Override
	public String toString() {
		return "CPMark [headTime=" + headTime + ", tailTime=" + tailTime + ", headCurr=" + headCurr + ", tailCurr="
				+ tailCurr + ", nearPoti=" + nearPoti + "]";
	}


}

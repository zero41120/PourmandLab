
public class CPMark {
	Double headTime = null;
	Double tailTime = null;
	Double headCurr = null;
	Double tailCurr = null;
	Double nearPoti = null;

	public boolean ready() {
		return (tailCurr == null)? false : true;
	}
	
	public Double getHeadTime() {
		return headTime;
	}

	public Double getTailTime() {
		return tailTime;
	}

	public Double getHeadCurr() {
		return headCurr;
	}

	public Double getTailCurr() {
		return tailCurr;
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
		this.headCurr = headCurr;
	}

	public void setTailCurr(Double tailCurr) {
		this.tailCurr = tailCurr;
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

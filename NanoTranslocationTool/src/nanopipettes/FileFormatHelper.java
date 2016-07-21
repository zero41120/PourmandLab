package nanopipettes;

import java.lang.invoke.ConstantCallSite;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.naming.spi.DirStateFactory.Result;

/**
 * This class is a string format checker. For the NanoTranslocationTool program,
 * this format checker will evaluate the input strings. If a string is the
 * output of Dr. Pourmand's research instrument, then the checker does nothing.
 * If a string is not part of reconizable output, the class always throw an
 * exception.
 * 
 * @author Tz-Shiuan Lin
 * 
 */
public class FileFormatHelper {

	static final String[] HEADERPATTERNS = { "^\".*[^\"]\"$", "^[0-9- \\t]+$",
			"^ATF[ \\t]+[0-9]*[\\.][0-9]*$", "^\"Signals=\".*$" };
	static final String DECLRATIONPATTERN = "^\"Time \\(s\\)\"[ \t](\"Trace #[0-9] \\([pAmV]*\\)\"[ \t])+\"Trace #[0-9] \\([mVpA]*\\)\"$";
	static final String DATAPATTERN = "^[1-9]?[.e\\d]*([ \\t][-.\\d]+){2}(([ \\t]{1}[-.\\d]+){2})*$";
	static String declrationString = "";
	static Integer traceCount = 0;
	//@formatter:off
	/*
	 * --The following is an example header--
	 * ATF	1.0
	 * 8	11     
	 * "AcquisitionMode=Episodic Stimulation"
	 * "Comment="
	 * "YTop=200000,1000"
	 * "YBottom=-200000,-1000"
	 * "SyncTimeUnits=5"
	 * "SweepStartTimesMS=0.000,10000.000,20000.000,30000.000,40000.000"
	 * "SignalsExported=Im_primar,Vm_sec"
	 * "Signals="	"Im_primar"	"Vm_sec"	"Im_primar"	"Vm_sec"	"Im_primar"	"Vm_sec"	"Im_primar"	"Vm_sec"	"Im_primar"	"Vm_sec"
	 *
	 * --The following is an example variable declaration--
	 * "Time (s)"	"Trace #1 (pA)"	"Trace #1 (mV)"	"Trace #2 (pA)"	"Trace #2 (mV)"	"Trace #3 (pA)"	"Trace #3 (mV)"	"Trace #4 (pA)"	"Trace #4 (mV)"	"Trace #5 (pA)"	"Trace #5 (mV)"
	 */
	//@formatter:on 
	
	/**
	 * This method checks the input line.
	 * 
	 * @param line
	 *            toCheck
	 * @throws RuntimeException
	 * @return Boolean
	 * @Case1: A header pattern. return false;
	 * @Case2: The declaration pattern. return true;
	 * @Case3: Any other pattern, throw RuntimeException.
	 */
	static public Boolean isDeclartion(String toCheck) throws RuntimeException {

		if (toCheck.matches(DECLRATIONPATTERN)) {
			StringTokenizer stk = new StringTokenizer(toCheck, "\t");
			traceCount = (stk.countTokens() - 1) / 2;
			return true;
		}
		for (String pattern : HEADERPATTERNS) {
			if (toCheck.matches(pattern)) {
				declrationString = toCheck;
				return false;
			}
		}
		throw new RuntimeException("TNError: Unrecognized input :" + toCheck);
	}
	
	static public TNData[] parseData(String line){
		if (line.matches(DATAPATTERN)) {
			TNData[] dataSet = new TNData[traceCount];
			StringTokenizer stk = new StringTokenizer(line, "\t");
			double time = Double.parseDouble(stk.nextToken());
			for (int i = 0; i < traceCount; i++) {
				dataSet[i] = new TNData(time, stk.nextToken(), stk.nextToken());
			}
			return dataSet;
		} else {
			return null;
		}
	}
	
	static public String parseSQL(String line){
		
	}
}

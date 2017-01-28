

"""
Script to parse the data

"""
import sys
import re

targetTime = "6.000e+1"
limitRate = 1
fReport = open("C:/Users/Nanopittet/Desktop/transloc/report.txt", "w")

def parseFile(target, targetTimeStr):
	fileData = list()
	num = re.compile(r"(-?[0-9].[0-9]{3}e[+-][0-9]+), (-?[0-9].[0-9]{3}e[+-][0-9]+)")
	for line in target:
		obj = num.match(line)
		if obj != None:
			pair = [float(obj.group(1)), float(obj.group(2))]
			fileData.append(pair)
			if obj.group(1) == targetTimeStr:
				break
	return fileData

def pickPeak(target):
	out = list()
	for i in range(len(target)):
		try:
			if target[i-1][1] < target [i][1] and target[i][1] > target[i+1][1]:
				out.append(target[i])
		except:
			return out
	return out

def getDiff(target):
	totalDif = 0
	maxDif = 0
	minDif = 0
	mystr = ""
	counter = 0
	for i in range(len(target)):
		try:
			thisDif = target[i][1]/target[i+1][1] - 1.0
			if thisDif == 0:
				continue
			else:
				# mystr += "dif: " + str(target[i][1]) + " " + str(target[i+1][1]) + " " + str(thisDif) + "\n"
				totalDif += thisDif
				counter += 1
				if thisDif > maxDif:
					maxDif = thisDif
				if thisDif < minDif:
					minDif = thisDif
		except IndexError:
			pass
	avgDiff = totalDif / counter
	return avgDiff, maxDif, minDif

def getSpike(target, limit):
	mystr = ""
	counter = 0
	for i in range(len(target)):
		try:
			thisDif = target[i][1]/target[i+1][1] - 1.0
			if thisDif == 0:
				continue
			elif thisDif < limit * limitRate:
				continue
			else:
				mystr += "Spike- time(" + str(target[i][0]) + ":" + str(target[i+1][0]) + ") "
				mystr += 		 "cur(" + str(target[i][1]) + ":" + str(target[i+1][1]) + ") \n"
				counter += 1
		except IndexError:
			pass
	fReport.write("Number of local max: " + str(len(target)) + "\n")
	fReport.write("Number of spikes: " + str(counter) + " with limit: "+ str(limit) +"\n")
	fReport.write(mystr + "\n\n")


    	

def main():
	while(1):
		pathControl = input("\n\nEnter control file path:")
		pathSample  = input("Enter sample file path:")
		try:
			fControl = open(pathControl, 'r')
			fSample  = open(pathSample, 'r')
		except:
			print("--Fail to open file.--")
			continue
		fCData = parseFile(fControl, targetTime)
		fSData = parseFile(fSample,  targetTime)
		fCPeak = pickPeak(fCData)
		fSPeak = pickPeak(fSData)
		cAvgDif, cMaxDif, cMinDif = getDiff(fCData)
		sAvgDif, sMaxDif, sMinDif = getDiff(fSData)
		cAvgPekDif, cMaxPekDif, cMinPekDif = getDiff(fCPeak)
		sAvgPekDif, sMaxPekDif, sMinPekDif = getDiff(fSPeak)

		mystr = "\n\nType(max%, avg%, min%)\n"
		
		mystr += "Control:" + str(cMaxDif * 100)    + str(cAvgDif* 100)    + str(cMinDif* 100) + "\n"
		mystr += "ConPeak:" + str(cMaxPekDif * 100) + str(cAvgPekDif* 100) + str(cMinPekDif* 100) + "\n"
		mystr += "Sample :" + str(sMaxDif * 100)    + str(sAvgDif* 100)    + str(sMinDif* 100) + "\n"
		mystr += "SamPeak:" + str(sMaxPekDif * 100) + str(sAvgPekDif* 100) + str(sMinPekDif* 100) + "\n"

		print(mystr)

		fReport.write("Report:" + mystr + "\n\n")
		getSpike(fSPeak, cAvgDif)
		getSpike(fSPeak, cAvgPekDif)

		fReport.close()



main()

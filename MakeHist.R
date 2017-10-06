# get commandline input

# print("HALLO WE'RE IN R")

#suppress output
# sin("/dev/null")

options = commandArgs(trailingOnly = TRUE)

# accept commandline options
pathToFile = options[1]
outName = options[2]
titelName = options[3]
legend = options[4]
suffix = options[5]
scaling = as.numeric(options [6])
measuredHits = as.numeric(options[7])


# # # option to test settings
# pathToFile = "out/tmp/GEFOS-LSBMD_hg19-trip1"
# measuredHits = 5
# outName = paste("out/test")
# titelName = "titel of graph"
# # legend = (measuredHits - mean(data)) / sd(data)
# legend = "NA"
# suffix = "png"
# scaling = 1.3
# isBed = TRUE


#open file containing input values
data = read.table(pathToFile,stringsAsFactors = FALSE)
data = data[,1]
# calculate axis length
yAxisMax = max(table(data))
xAxisMax = round(max(max(data), measuredHits) * 1.2 )
xAxisMin = round(min(min(data), measuredHits) * 0.8 )

# define bins
xBinMax = (max(max(data), measuredHits) + 0.5)
xBinMin = (min(min(data), measuredHits) - 0.5)
bins = seq(from = xBinMin, to = xBinMax, by = 1)

# check if png or pdf chosen then create respectivly
if ( suffix == "png" ){
	# init plot as png
	outName = paste(outName, ".png", sep ="")
	png(file = outName)
} else if ( suffix == "pdf") {
	# init plot as pdf
	outName = paste(outName, ".pdf", sep = "")
	pdf(file = outName)
	
} else {
	print (paste("Filename ", outName))
  print("HALLO")
	
}

# make histrogram
hist(data, 
		 xlim = c(xAxisMin-0.5, xAxisMax), 
		 col = "grey",
		 breaks = bins,
		 main = titelName,
		 xlab = "number of loci found",
		 ylab = "number of observations",
		 cex.lab = scaling,
		 cex.main = scaling,
		 cex.axis = scaling
)

# if (!isBed){

  # draw line for extracted amound of loci and lable with number of found loci
  abline(v = measuredHits, col = "black", lwd = 5)
  # print (measuredHits)
  # print (yAxisMax)
  # print (scaling)
  text((measuredHits * 1.05 ), (yAxisMax * (3/4)), paste(measuredHits, " loci found"), srt=90, cex = scaling)
  
# }

# write zScore in Plot
if (!is.na(legend)){
	legend("top",
				 paste(legend),
				 bty = "n",
				 cex = scaling
	)
} 

dev.off()

# sink()

clear

#path to GenToS
GenToS="/path/to/GenToS/gentos"

# run GenToS for each List with plenty option
# estimate enrichment by binomial distribution
$GenToS \
-specFile spec.file \
-listCollection list.collection \
-plenty \
-enrichment

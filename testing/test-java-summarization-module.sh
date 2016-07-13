#!/bin/bash

set -e
relative_path=`dirname $0`
root=`cd $relative_path;pwd`
project=$root/../summarization

echo "Unit testing the java summarization module"
cd $project
java -Xms256m -Xmx1g -cp .:'summarization.jar' org.junit.runner.JUnitCore it.unimib.disco.summarization.test.unit.UnitTests

#----------------------------------------------------------------------------------------------------------------------------------------------------------------------------  
# perchè quando i test di unità sulle classi AKPDatatypeCount e ObjectTypeCount sono chiamati vengono creati questi file e successivamente nella summarization di 
# system-test viene scritto sopra senza cancellare causando errori nei test di sistema. Purtroppo queste due classi sono le uniche che hanno triple e rispettivi AKPS. 

rm datatype-akp_grezzo.txt  
rm object-akp_grezzo.txt    
#-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

cd $root


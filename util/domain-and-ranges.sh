#!/bin/bash

function run_experiment(){
	echo "*************** Running experiment $@ ***************"
	echo
	java -Xms256m -Xmx16g -cp .:'summarization.jar' it.unimib.disco.summarization.experiments.$@
	echo "*************** done ***************"
	echo
}

set -e
relative_path=`dirname $0`
root=`cd $relative_path;pwd`
project=$root/../summarization

cd $project

run_experiment DomainsAndRanges dbpedia-2014 dbpedia.org benchmark/experiments/dbpedia/dbpedia_2014.owl /Users/anisarula/Documents/git/projects/abstat/data/experiments/domainAndRangeResults

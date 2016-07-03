#! /bin/bash

set -e

version=$1
include_raw=$2
dbpedia_downloads="http://downloads.dbpedia.org/${version}"

target_directory=/home/renzo/rAlvaPrincipe/abstat/data/datasets/dbpedia-$1$include_raw
rm -rf $target_directory
mkdir -p $target_directory

ontology_directory=$target_directory/ontology
mkdir $ontology_directory
wget "$dbpedia_downloads/dbpedia_$version.owl" -P $ontology_directory
#bunzip2 "$ontology_directory/dbpedia_$version.owl.bz2"

triples_directory=$target_directory/triples
wget "$dbpedia_downloads/core-i18n/en/instance_types_en.ttl.bz2" -P $triples_directory
bunzip2 $triples_directory/instance_types_en.ttl.bz2
wget "$dbpedia_downloads/core-i18n/en/mappingbased_literals_en.ttl.bz2" -P $triples_directory
bunzip2 $triples_directory/mappingbased_literals_en.ttl.bz2
wget "$dbpedia_downloads/core-i18n/en/mappingbased_objects_en.ttl.bz2" -P $triples_directory
bunzip2 $triples_directory/mappingbased_objects_en.ttl.bz2
wget "$dbpedia_downloads/core-i18n/en/persondata_en.ttl.bz2" -P $triples_directory
bunzip2 $triples_directory/persondata_en.ttl.bz2
wget "$dbpedia_downloads/core-i18n/en/specific_mappingbased_properties_en.ttl.bz2" -P $triples_directory
bunzip2 $triples_directory/specific_mappingbased_properties_en.ttl.bz2
if [[ $include_raw == -infobox ]]
then
        wget "$dbpedia_downloads/core-i18n/en/infobox_properties_en.ttl.bz2" -P $triples_directory
        bunzip2 "$triples_directory/infobox_properties_en.ttl.bz2"
fi

cat $triples_directory/*.ttl | grep -v "# started" > $triples_directory/dataset
rm $triples_directory/*.ttl
mv $triples_directory/dataset $triples_directory/dataset.nt #prima era ttl
#java -jar $scripts_directory/rdf2rdf-1.0.1-2.3.1.jar $triples_directory/dataset.ttl $triples_directory/dataset.nt
#rm $triples_directory/dataset.ttl







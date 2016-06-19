#! /bin/bash

set -e

version=$1
include_raw=$2
dbpedia_downloads="http://downloads.dbpedia.org/${version}"

relative_path=`dirname $0`   
root=`cd $relative_path;pwd`
target_directory=$root/../data/datasets/dbpedia-italiana-$1$include_raw
rm -rf $target_directory
mkdir -p $target_directory

ontology_directory=$target_directory/ontology
mkdir $ontology_directory
triples_directory=$target_directory/triples


echo "---Start: Downloading ontology---" echo ""

wget "$dbpedia_downloads/dbpedia_$version.owl" -P $ontology_directory

echo "---End: Downloading ontology---" echo ""

echo "---Start: Downloading dataset---" echo ""

wget "$dbpedia_downloads/core-i18n/it/instance_types_it.ttl.bz2" -P $triples_directory
bunzip2 $triples_directory/instance_types_it.ttl.bz2
wget "$dbpedia_downloads/core-i18n/it/mappingbased_literals_it.ttl.bz2" -P $triples_directory
bunzip2 $triples_directory/mappingbased_literals_it.ttl.bz2
wget "$dbpedia_downloads/core-i18n/it/mappingbased_objects_it.ttl.bz2" -P $triples_directory
bunzip2 $triples_directory/mappingbased_objects_it.ttl.bz2
#wget "$dbpedia_downloads/core-i18n/en/persondata_en.ttl.bz2" -P $triples_directory
#bunzip2 $triples_directory/persondata_en.ttl.bz2
wget "$dbpedia_downloads/core-i18n/it/specific_mappingbased_properties_it.ttl.bz2" -P $triples_directory
bunzip2 $triples_directory/specific_mappingbased_properties_it.ttl.bz2
if [[ $include_raw == -infobox ]]
then
        wget "$dbpedia_downloads/core-i18n/it/infobox_properties_it.ttl.bz2" -P $triples_directory
        bunzip2 "$triples_directory/infobox_properties_it.ttl.bz2"
fi

echo "---End: Downloading dataset---" echo ""


echo "---Start: Merging dataset pieces---" echo ""

cat $triples_directory/*.ttl | grep -v "# started" > $triples_directory/dataset
rm $triples_directory/*.ttl
mv $triples_directory/dataset $triples_directory/dataset.nt #prima era ttl

echo "---End: Merging dataset pieces---" echo ""

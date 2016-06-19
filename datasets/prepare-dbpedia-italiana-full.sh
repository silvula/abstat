#! /bin/bash

set -e

version=$1
include_raw=$2
dbpedia_downloads="http://it.dbpedia.org/downloads" 

relative_path=`dirname $0`   
root=`cd $relative_path;pwd`
target_directory=$root/../data/datasets/dbpedia-$1$include_raw
rm -rf $target_directory
mkdir -p $target_directory

ontology_directory=$target_directory/ontology
mkdir $ontology_directory
triples_directory=$target_directory/triples


echo "---Start: Downloading ontology---" echo ""

wget "http://downloads.dbpedia.org/2015-10/dbpedia_2015-10.owl" -P $ontology_directory

echo "---End: Downloading ontology---" echo ""


echo "---Start: Downloading dataset---" echo ""

wget "$dbpedia_downloads/20150121/dbtax-instance-types.ttl.gz" -P $triples_directory
gunzip $triples_directory/dbtax-instance-types.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-wikipedia-links.ttl.gz" -P $triples_directory
gunzip $triples_directory/itwiki-20150121-wikipedia-links.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-labels.ttl.gz" -P $triples_directory
gunzip $triples_directory/itwiki-20150121-labels.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-article-templates.ttl.gz" -P $triples_directory
gunzip $triples_directory/itwiki-20150121-article-templates.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-geo-coordinates.ttl.gz" -P $triples_directory
gunzip $triples_directory/itwiki-20150121-geo-coordinates.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-redirects.ttl.gz" -P $triples_directory
gunzip $triples_directory/itwiki-20150121-redirects.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-page-ids.ttl.gz" -P $triples_directory
gunzip $triples_directory/itwiki-20150121-page-ids.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-airpedia.ttl.gz" -P $triples_directory
gunzip $triples_directory/itwiki-20150121-airpedia.ttl.gz
wget "$dbpedia_downloads/20150121/wikidatawiki-20150126-available_interlanguage-links.ttl.gz" -P $triples_directory
gunzip $triples_directory/wikidatawiki-20150126-available_interlanguage-links.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-revision-uris.ttl.gz" -P $triples_directory
gunzip $triples_directory/itwiki-20150121-revision-uris.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-revision-ids.ttl.gz" -P $triples_directory
gunzip $triples_directory/itwiki-20150121-revision-ids.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-skos-categories.ttl.gz" -P $triples_directory
gunzip $triples_directory/itwiki-20150121-skos-categories.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-mappingbased-properties.ttl.gz" -P $triples_directory
gunzip $triples_directory/itwiki-20150121-mappingbased-properties.ttl.gz
wget "$dbpedia_downloads//20150121/itwiki-20150121-category-labels.ttl.gz" -P $triples_directory
gunzip $triples_directory/itwiki-20150121-category-labels.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-disambiguations.ttl.gz" -P $triples_directory
gunzip $triples_directory/itwiki-20150121-disambiguations.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-instance-types.ttl.gz" -P $triples_directory
gunzip $triples_directory/itwiki-20150121-instance-types.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-homepages.ttl.gz" -P $triples_directory
gunzip $triples_directory/itwiki-20150121-homepages.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-short-abstracts.ttl.gz" -P $triples_directory
gunzip $triples_directory/itwiki-20150121-short-abstracts.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-long-abstracts.ttl.gz" -P $triples_directory
gunzip $triples_directory/itwiki-20150121-long-abstracts.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-infobox-property-definitions.ttl.gz" -P $triples_directory     #Da mettere in una versione -infobox?
gunzip $triples_directory/itwiki-20150121-infobox-property-definitions.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-page-links.ttl.gz" -P $triples_directory
gunzip $triples_directory/itwiki-20150121-page-links.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-topical-concepts.ttl.gz" -P $triples_directory
gunzip $triples_directory/itwiki-20150121-topical-concepts.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-images.ttl.gz" -P $triples_directory
gunzip $triples_directory/itwiki-20150121-images.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-specific-mappingbased-properties.ttl.gz" -P $triples_directory
gunzip $triples_directory/itwiki-20150121-specific-mappingbased-properties.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-external-links.ttl.gz" -P $triples_directory
gunzip $triples_directory/itwiki-20150121-external-links.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-article-categories.ttl.gz" -P $triples_directory
gunzip $triples_directory/itwiki-20150121-article-categories.ttl.gz
wget "$dbpedia_downloads/20150121/itwiki-20150121-infobox-properties.ttl.gz" -P $triples_directory              #Da mettere in una version -infobox?
gunzip $triples_directory/itwiki-20150121-infobox-properties.ttl.gz
wget "$dbpedia_downloads/20150121/wikidatawiki-20150126-wikidata-sameas.ttl.gz" -P $triples_directory
gunzip $triples_directory/wikidatawiki-20150126-wikidata-sameas.ttl.gz

#if [[ $include_raw == -infobox ]]
#then
#        wget "$dbpedia_downloads/core-i18n/en/infobox_properties_en.ttl.bz2" -P $triples_directory
#        bunzip2 "$triples_directory/infobox_properties_en.ttl.bz2"
#fi

echo "---End: Downloading dataset---" echo ""

echo "---Start: Merging dataset pieces---" echo ""

cat $triples_directory/*.ttl | grep -v "# started" > $triples_directory/dataset
rm $triples_directory/*.ttl
mv $triples_directory/dataset $triples_directory/dataset.nt #prima era ttl

echo "---End: Merging dataset pieces---" echo ""

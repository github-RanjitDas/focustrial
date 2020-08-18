#!/bin/bash
#This file merges the 3 mutations.xml files created on pitest run into 1

#Create reports directory if it doesn't exist already
report_directory="build/reports/pitest"
mkdir -p $report_directory

output_file=$report_directory"/mutations.xml"

mutation_identifier='<mutation\s' #pattern to identify lines with mutations

#Header of XML file
head -2 data/build/reports/pitest/mutations.xml > $output_file

#Mutations of each layer
# shellcheck disable=SC2002,SC2129
cat data/build/reports/pitest/mutations.xml | grep $mutation_identifier >> $output_file
# shellcheck disable=SC2002
cat domain/build/reports/pitest/mutations.xml | grep $mutation_identifier >> $output_file
# shellcheck disable=SC2002
cat presentation/build/reports/pitest/mutations.xml | grep $mutation_identifier >> $output_file

#Close XML file
tail -1 data/build/reports/pitest/mutations.xml >> $output_file

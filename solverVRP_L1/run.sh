#!/bin/bash

myprog='java -jar solverVRP.jar'
while read file; do
  echo $file
  echo $myprog -i $file
  $myprog -i $file  
done < files.txt

#!/bin/bash
#e.g. sh spr2txt.sh list.121204.spr list.121204.txt
sprfile=$1
txtfile=$2
>${txtfile}
id=0
cat ${sprfile} | while read line
do
  echo ${line} | sed -e "s/\^/*/g" | sed -e "s/[a-zA-Z]//g" | sed -e "s/[.,:'_\-\(\)]/ /g" | sed -e "s/  */ /g" | sed -e "s/#/#$[id] /g" >>${txtfile}
  case ${line} in
    \#*)
      id=$[id+1]
      ;;
  esac
done

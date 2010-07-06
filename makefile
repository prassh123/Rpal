#!/bin/bash
export CLASSPATH := ./parser:./lib/commons-cli-1.2.jar
export PATH := ./rpal:${PATH}

all:
        javac parser/*.java
cl:
        rm -f parser/*.class
run:
        java P1 $(OPT) $(TESTFILE)   
test:
        chmod +x difftest.pl p1
        chmod 777 ./rpal/tests/*
        chmod +x ./rpal/rpal
        ./difftest.pl -1 "./rpal/rpal -ast -noout FILE" -2 "./p1 -ast FILE" -t ./rpal/tests/ 

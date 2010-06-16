all:
	javac *.java
cl:
	rm -f *.class
run:
	java p1
test:
	./difftest.pl -1 "./rpal -ast -noout FILE" -2 "java P1 -ast -noout FILE" -t ~/rpal/tests/
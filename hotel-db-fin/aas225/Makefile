JC = javac
CP = .:ojdbc8.jar
MAIN_AAS225 = aas225
MAIN_HOUSEKEEPING = Housekeeping
MAIN_CUSTOMER = Customer
MAIN_FRONT_DESK = FrontDesk

.SUFFIXES: .java .class

.java.class:
	$(JC) -cp $(CP) $*.java

default: classes run

classes: $(MAIN_AAS225).class $(MAIN_HOUSEKEEPING).class $(MAIN_CUSTOMER).class $(MAIN_FRONT_DESK).class

run:
	java -cp $(CP) $(MAIN_AAS225)

clean:
	$(RM) *.class

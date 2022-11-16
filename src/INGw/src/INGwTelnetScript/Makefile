PROGRAM = INGwProbe

LIBS = -lresolv -lsocket -lnsl -lpthread

LIBDIRS = -L./bin 


# list of source files
CXXSOURCES = exceptions.cpp \
             logger.cpp \
             tcpconn.cpp \
             cmdvalidator.cpp \
             cmdhandler.cpp \
             cmd.cpp \
             thread.cpp \
             util.cpp \
             inputhandlerthread.cpp \
             responsehandlerthread.cpp \
             configmanager.cpp \
             logmanager.cpp \
             ingwprobemain.cpp 


CXXOBJECTS = $(CXXSOURCES:.cpp=.o)  # expands to list of object files

#CXXFLAGS = -g -DPROMPT
CXXFLAGS = -g

CXX = CC

LDFLAGS = $(LIBDIRS) $(LIBS)

all: $(PROGRAM)

$(PROGRAM): $(CXXOBJECTS)
	$(CXX) -o $@ $(CXXOBJECTS) $(LDFLAGS)

exceptions.o: INGwTelnetScript/exceptions.cpp INGwTelnetScript/exceptions.h
	$(CXX) $(CXXFLAGS) -c -o exceptions.o INGwTelnetScript/exceptions.cpp

inputhandlerthread.o: INGwTelnetScript/inputhandlerthread.cpp INGwTelnetScript/inputhandlerthread.h
	$(CXX) $(CXXFLAGS) -c -o inputhandlerthread.o INGwTelnetScript/inputhandlerthread.cpp

logger.o: INGwTelnetScript/logger.cpp INGwTelnetScript/logger.h
	$(CXX) $(CXXFLAGS) -c -o logger.o INGwTelnetScript/logger.cpp

thread.o: INGwTelnetScript/thread.cpp INGwTelnetScript/thread.h
	$(CXX) $(CXXFLAGS) -c -o thread.o INGwTelnetScript/thread.cpp

util.o: INGwTelnetScript/util.cpp INGwTelnetScript/util.h
	$(CXX) $(CXXFLAGS) -c -o util.o INGwTelnetScript/util.cpp

tcpconn.o: INGwTelnetScript/tcpconn.cpp INGwTelnetScript/tcpconn.h
	$(CXX) $(CXXFLAGS) -c -o tcpconn.o INGwTelnetScript/tcpconn.cpp

cmd.o: INGwTelnetScript/cmd.cpp INGwTelnetScript/cmd.h
	$(CXX) $(CXXFLAGS) -c -o cmd.o INGwTelnetScript/cmd.cpp

cmdvalidator.o: INGwTelnetScript/cmdvalidator.cpp INGwTelnetScript/cmdvalidator.h
	$(CXX) $(CXXFLAGS) -c -o cmdvalidator.o INGwTelnetScript/cmdvalidator.cpp

cmdhandler.o: INGwTelnetScript/cmdhandler.cpp INGwTelnetScript/cmdhandler.h
	$(CXX) $(CXXFLAGS) -c -o cmdhandler.o INGwTelnetScript/cmdhandler.cpp


responsehandlerthread.o: INGwTelnetScript/responsehandlerthread.cpp INGwTelnetScript/responsehandlerthread.h
	$(CXX) $(CXXFLAGS) -c -o responsehandlerthread.o INGwTelnetScript/responsehandlerthread.cpp

configmanager.o: INGwTelnetScript/configmanager.cpp INGwTelnetScript/configmanager.h
	$(CXX) $(CXXFLAGS) -c -o configmanager.o INGwTelnetScript/configmanager.cpp

logmanager.o: INGwTelnetScript/logmanager.cpp  INGwTelnetScript/logmanager.h
	$(CXX) $(CXXFLAGS) -c -o logmanager.o INGwTelnetScript/logmanager.cpp

ingwprobemain.o: INGwTelnetScript/ingwprobemain.cpp 
	$(CXX) $(CXXFLAGS) -c -o ingwprobemain.o INGwTelnetScript/ingwprobemain.cpp


clean:
	$(RM) $(CXXOBJECTS) $(PROGRAM)

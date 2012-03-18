# Makefile for Mazewar
# $Id: Makefile 357 2004-01-31 19:34:39Z geoffw $

#JIKES=jikes +P -source 1.4
# The jikes is *way* faster if you have it.
#JAVAC=${JIKES}
# Otherwise use Sun's compiler.
JAVA_HOME=/cad2/ece419s/java/jdk1.6.0/
JAVAC=${JAVA_HOME}/bin/javac -source 1.6
JAVADOC=${JAVA_HOME}/bin/javadoc -use -source 1.6 -author -version -link http://java.sun.com/j2se/1.6.0/docs/api/ 
MKDIR=mkdir
RM=rm -rf
CAT=cat

# The only file we are interested in is Mazewar.class,
# the rest are handled by the dependencies.
FILES=Mazewar.class

all: 	MazewarServer.java Mazewar.java Cell.java Client.java ClientEnqueueHandler.java ClientEvent.java ClientListener.java ClientQueue.java DirectedPoint.java Direction.java GUIClient.java LocalClient.java Maze.java MazeImpl.java MazeListener.java MazewarPacket.java MazewarServer.java MazewarServerHandlerThread.java OverheadMazePanel.java PlayersQueue.java Point.java Projectile.java RemoteClient.java RobotClient.java ScoreTableModel.java ServerQueue.java SortedMultiSet.java
	${JAVA_HOME}/bin/javac *.java


# Rule for making classes.
%.class : %.java 
	${JAVAC} $<

# Build dependencies.  Need jikes to do this.
dep:
	${JIKES} +M *.java
	${CAT} *.u > Makefile.dep
	${RM} *.u

# Create documentation using javadoc
docs:
	${JAVADOC} -d docs *.java

# Clean up
clean: 
	${RM} *.class *~ docs/*

# Classfile dependencies
include Makefile.dep

# docs isn't a real target
.PHONY : docs


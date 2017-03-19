# JavaDumper

Simple to dump Java class to file

Useage:

	Java {all your javaagent, agentlib, agentpath} -javaagent:JavaDumper.jar=<class to dump>;<where to save> {other}

E.g.:

	java -javaagent:JavaDumper.jar=java.lang.Shutdown;Shutdown.class -jar JavaDumper.jar
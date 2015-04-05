

# Yelli #
Yelli is a mobile app that lets you share your location in realtime. You can learn more at [bokwas.com/yelli](http://bokwas.com/yelli/) .

![Yelli Demo](http://bokwas.com/yelli/upload/yelli_image.gif)


##Stack##
Yelli stack consists of three major parts viz. websocket server, android-client, web-client. The websocket server is implemented using Glassfish Tyrus in Java. The android-client is built using Java and the web-client using jQuery and Google Maps. 

##Server Deployment##
* First build the server project by running this command in server/ directory. Please ensure you have maven installed in your machine before you run this command.
	> mvn clean install -DskipTests

* The compiled jar file will be available in server/target/Yelli-0.0.1-SNAPSHOT-jar-with-dependencies.jar

* At this point you're ready to deploy the server. You can do this by running this command. Make sure that you run the command in the remote machine where you want to run the server. Your server is up and running at this point, listening to websocket requests at port 10023.
> java -cp Yelli-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.yelli.App

    
    
    			
		
> For further help or feedback, contact [Susheel](mailto:susheel.s2k@gmail.com) 
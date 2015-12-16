package com.ibm.nrdaemon.operations;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;
import javax.jms.*;
import javax.naming.*;

/** This class sends the New Relic Response data to the Wildfly AS */
public class Publisher implements ExceptionListener{

    private static final Logger log = Logger.getLogger(Publisher.class.getName());
    private Connection connection = null;
    private MessageProducer producer;
    private Session queueSession;
    private Context context;

    /** This is a hack to access properties files when debugging*/
    protected boolean debug = true;

    protected void Publish(String JSONData) throws Throwable {

        try {
            /** Get the initial context */
            final Properties props = new Properties();
            /** If debugging in IDE the properties are acceded this way */
            if(debug){
                InputStream f = getClass().getClassLoader().getResourceAsStream("publisher.properties");
                props.load(f);
            }
            /** If running the .jar artifact the properties are acceded this way*/
            else{
                File jarPath = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
                String propertiesPath = jarPath.getParentFile().getAbsolutePath();
                props.load(new FileInputStream(propertiesPath+ File.separator + "publisher.properties"));
            }

            /** These few lines should be removed and setup in the properties file*/
            props.put(Context.INITIAL_CONTEXT_FACTORY, props.getProperty("INITIAL_CONTEXT_FACTORY"));
            props.put(Context.PROVIDER_URL, props.getProperty("PROVIDER_URL"));
            props.put(Context.SECURITY_PRINCIPAL, props.getProperty("DEFAULT_USERNAME"));
            props.put(Context.SECURITY_CREDENTIALS, props.getProperty("DEFAULT_PASSWORD"));
            context = new InitialContext(props);

            /** Lookup the queue object */
            Queue queue = (Queue) context.lookup(props.getProperty("DEFAULT_DESTINATION"));

            /** Lookup the queue connection factory */
            ConnectionFactory connFactory = (ConnectionFactory) context.lookup(props.getProperty("DEFAULT_CONNECTION_FACTORY"));

            /** Create a queue connection */
            connection = connFactory.createConnection(props.getProperty("DEFAULT_USERNAME"), props.getProperty("DEFAULT_PASSWORD"));

            /** Create a queue session */
            queueSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            /** Create a queue producer */
            producer = queueSession.createProducer(queue);

            /** Start connection */
            connection.start();

            /** Send the data */
            this.sendMessage(JSONData);

        } catch (Exception e) {
            log.severe(e.getMessage());
            throw e;
        }finally {
            this.finalize();
        }
    }

    public void sendMessage(String newRelicData) {
        try {
            TextMessage message = queueSession.createTextMessage(newRelicData);
            producer.send(message);
        } catch (JMSException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onException(JMSException exception) {
        System.err.println("an error occurred: " + exception);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (context != null) {
                context.close();
            }
            /**  Closing the connection takes care of the session, producer, and consumer */
            if (connection != null) {
                connection.close();
            }
        }
        finally {
            super.finalize();
        }
    }
}

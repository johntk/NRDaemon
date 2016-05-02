package com.ibm.nrdaemon.operations;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import javax.jms.*;
import javax.naming.*;

import org.apache.log4j.Logger;


/**
 * This class sends the New Relic Response data to the Wildfly AS
 */
public class Publisher implements ExceptionListener {

    private static Logger logger = Logger.getLogger(Publisher.class.getName());
    //    private static final Logger log = Logger.getLogger(Publisher.class.getName());
    private Connection connection = null;
    private Context context;

    /**
     * This is a hack to access properties files when debugging
     */
    protected boolean debug = true;

    protected void Publish(String JSONData) throws Throwable {
        while (true) {

            try {
                /** Get the initial context */
                final Properties props = new Properties();

                /** If debugging in IDE the properties are acceded this way */
                if (debug) {
                    try (InputStream f = getClass().getClassLoader().getResourceAsStream("publisher.properties")) {
                        props.load(f);
                    }
                }
                /** If running the .jar artifact the properties are acceded this way*/
                else {
                    File jarPath = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
                    String propertiesPath = jarPath.getParentFile().getAbsolutePath();
                    props.load(new FileInputStream(propertiesPath + File.separator + "publisher.properties"));
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
                try (Connection connection = connFactory.createConnection(props.getProperty("DEFAULT_USERNAME"), props.getProperty("DEFAULT_PASSWORD"));

                     /** Create a queue session */
                     Session queueSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                     /** Create a queue producer */
                     MessageProducer producer = queueSession.createProducer(queue)) {

                    /** Start connection */
                    connection.start();

                    /** Send the data */
                    TextMessage message = queueSession.createTextMessage(JSONData);
                    producer.send(message);
                    System.out.println("Message Sent! " + JSONData);
                    break;
                }
            } catch (Exception e) {
                logger.fatal("Exception happen Publisher class!1", e);
            } finally {
                if (context != null) {
                    context.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }
        }
    }

    @Override
    public void onException(JMSException exception) {
        logger.fatal("Exception happen Publisher class!2", exception);
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
        } finally {
            super.finalize();
        }
    }
}

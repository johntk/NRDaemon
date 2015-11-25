package com.ibm.nrdaemon;

/**
 * Created by John TK on 20/11/2015.
 */
import java.util.Properties;
import java.util.logging.Logger;
import javax.jms.*;
import javax.naming.*;

public class Publisher implements ExceptionListener{

    /** Set up all the default values
     *  Possibly put these into a properties file when refining */
    private static final Logger log = Logger.getLogger(Publisher.class.getName());
    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String DEFAULT_DESTINATION = "java:/jms/queue/demoQueue";
    private static final String DEFAULT_USERNAME = "jmsuser";
    private static final String DEFAULT_PASSWORD = "babog2001";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String PROVIDER_URL = "http-remoting://johnkiernan.ie:80";
    private Connection connection = null;
    private MessageProducer producer;
    private Session queueSession;
    private Context context;

    protected void Publish(String JSONData) throws Throwable {

        try {
            /** Get the initial context */
            final Properties props = new Properties();
            props.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            props.put(Context.PROVIDER_URL, PROVIDER_URL);
            props.put(Context.SECURITY_PRINCIPAL, DEFAULT_USERNAME);
            props.put(Context.SECURITY_CREDENTIALS, DEFAULT_PASSWORD);
            context = new InitialContext(props);

            /** Lookup the queue object */
            Queue queue = (Queue) context.lookup(DEFAULT_DESTINATION);

            /** Lookup the queue connection factory */
            ConnectionFactory connFactory = (ConnectionFactory) context.lookup(DEFAULT_CONNECTION_FACTORY);

            /** Create a queue connection */
            connection = connFactory.createConnection(DEFAULT_USERNAME, DEFAULT_PASSWORD);

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

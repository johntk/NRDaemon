package com.ibm.nrdaemon;

/**
 * Created by John TK on 20/11/2015.
 */
import java.util.Properties;
import java.util.logging.Logger;
import javax.jms.*;
import javax.naming.*;

public class Publisher {

    /**
     * Set up all the default values
     * Possibly put these into a properties file
     */
    private static final Logger log = Logger.getLogger(Publisher.class.getName());
    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String DEFAULT_DESTINATION = "java:/jms/queue/demoQueue";
    private static final String DEFAULT_MESSAGE_COUNT = "1";
    private static final String DEFAULT_USERNAME = "jmsuser";
    private static final String DEFAULT_PASSWORD = "babog2001";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String PROVIDER_URL = "http-remoting://192.168.1.6:8080";

    public Publisher(){

    }/**
     * Create the JMS connection, session and producer
     */
    protected void Send(String JSONData) throws Exception{

        ConnectionFactory connectionFactory;
        Connection connection = null;
        Context context = null;
        MessageProducer producer;
        Destination destination;
        TextMessage message;
        Session session;

        try {
            /**
             *Set up the context for the JNDI
             */
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, System.getProperty(Context.PROVIDER_URL, PROVIDER_URL));
            env.put(Context.SECURITY_PRINCIPAL, System.getProperty("username", DEFAULT_USERNAME));
            env.put(Context.SECURITY_CREDENTIALS, System.getProperty("password", DEFAULT_PASSWORD));
            context = new InitialContext(env);

            /**
             * lookup Perform the JNDI lookups
             */
            String connectionFactoryString = System.getProperty("connection.factory", DEFAULT_CONNECTION_FACTORY);
            log.info("Attempting to acquire connection factory \"" + connectionFactoryString + "\"");
            connectionFactory = (ConnectionFactory) context.lookup(connectionFactoryString);
            log.info("Found connection factory \"" + connectionFactoryString + "\" in JNDI");

            String destinationString = System.getProperty("destination", DEFAULT_DESTINATION);
            log.info("Attempting to acquire destination \"" + destinationString + "\"");
            destination = (Destination) context.lookup(destinationString);
            log.info("Found destination \"" + destinationString + "\" in JNDI");

            /**
             * Create the JMS connection, session, producer, and consumer
             */
            connection = connectionFactory.createConnection(System.getProperty("username", DEFAULT_USERNAME), System.getProperty("password", DEFAULT_PASSWORD));
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            connection.start();

            int count = Integer.parseInt(System.getProperty("message.count", DEFAULT_MESSAGE_COUNT));
            String content = System.getProperty("message.content", JSONData);

            log.info("Sending " + count + " messages with content: " + content);

            /**
             * Send the specified number of messages
             */
            for (int i = 0; i < count; i++) {
                message = session.createTextMessage(content);
                producer.send(message);
            }

        } catch (Exception e) {
            log.severe(e.getMessage());
            throw e;
        } finally {
            if (context != null) {
                context.close();
            }

            /**
             * closing the connection takes care of the session, producer, and consumer
             */
            if (connection != null) {
                connection.close();
            }
        }
    }
}

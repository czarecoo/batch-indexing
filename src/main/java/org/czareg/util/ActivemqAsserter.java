package org.czareg.util;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.czareg.IndexEvent;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class ActivemqAsserter {
    public static void main(String[] args) {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616");

        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(activeMQConnectionFactory);
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestinationName("indexing.queue");
        jmsTemplate.setConnectionFactory(cachingConnectionFactory);
        jmsTemplate.setReceiveTimeout(500);

        Set<String> uuids = new HashSet<>();
        List<String> duplicates = new ArrayList<>();

        while (true) {
            String uuid = (String) jmsTemplate.receiveAndConvert();

            if (uuid == null) {
                // queue drained
                break;
            }

            boolean added = uuids.add(uuid);
            if(!added){
                System.out.println("Duplicate: "+uuid);
                duplicates.add(uuid);
            }
        }

        String collect = uuids.stream().map(Long::valueOf).sorted().map(String::valueOf).collect(Collectors.joining(","));
        System.out.println(collect);

        Set<String> expected = LongStream.range(0, 10_000).mapToObj(String::valueOf).collect(Collectors.toSet());
        System.out.println("all expected uuids: " + expected.equals(uuids));

        System.out.println(duplicates);
    }
}

package org.olf;

import static grails.async.Promises.*
import static groovy.json.JsonOutput.*

import org.apache.kafka.clients.consumer.KafkaConsumer

import com.k_int.web.toolkit.async.WithPromises
import grails.async.Promise
import grails.core.GrailsApplication
import grails.events.EventPublisher
import grails.events.annotation.Subscriber
import groovy.json.JsonSlurper
import grails.web.databinding.DataBinder
import grails.gorm.multitenancy.Tenants


public class EventConsumerService implements EventPublisher, DataBinder {
  GrailsApplication grailsApplication

  private KafkaConsumer consumer = null;
  private boolean running = true;
  private boolean tenant_list_updated = false;
  private Set tenant_list = null;

  @javax.annotation.PostConstruct
  public void init() {
    log.debug("Configuring event consumer service")
    Properties props = new Properties()
    try {
      grailsApplication.config.events.consumer.toProperties().each { final String key, final String value ->
        // Directly access each entry to cause lookup from env
        String prop = grailsApplication.config.getProperty("events.consumer.${key}")
        log.debug("Configuring event consumer service :: key:${key} value:${value} prop:${prop}");
        props.setProperty(key, prop)
      }
      log.debug("Configure consumer ${props}")
    }
    catch ( Exception e ) {
      log.error("Problem assembling props for consume",e);
    }

    consumer = new KafkaConsumer(props)

    /*
     * https://github.com/confluentinc/kafka-streams-examples/blob/5.4.1-post/src/main/java/io/confluent/examples/streams/WordCountLambdaExample.java
     * suggests this hook when using streams... We need to do something similar
     * Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
     */

    Promise p = WithPromises.task {
      consumeLogEvents();
    }

    p.onError { Throwable err ->
      log.warn("Problem with consumer",e);
    }

    p.onComplete { result ->
      log.debug("Consumer exited cleanly");
    }

    log.debug("EventConsumerService::init() returning");
  }

  private void consumeLogEvents() {

    try {
      while ( running ) {

        def topics = null;
        if ( ( tenant_list == null ) || ( tenant_list.size() == 0 ) )
          topics = ['dummy_topic']
        else
          topics = tenant_list.collect { "${it}_mod_service_interaction_LogEvents".toString() }
        log.debug("Listening out for topics : ${topics}");
        tenant_list_updated = false;
        consumer.subscribe(topics)
        while ( ( tenant_list_updated == false ) && ( running == true ) ) {
          def consumerRecords = consumer.poll(1000)
          consumerRecords.each{ record ->
            try {
              log.debug("KAFKA_EVENT:: topic: ${record.topic()} Key: ${record.key()}, Partition:${record.partition()}, Offset: ${record.offset()}, Value: ${record.value()}");

              if ( record.topic.contains('_mod_service_interaction_LogEvents') ) {
                // Convert the JSON payload string to a map 
                def jsonSlurper = new JsonSlurper()EventConsumerSer
                def data = jsonSlurper.parseText(record.value)
                

                /* TODO right now we just grab this data and write it in a log message,
                 * will eventually need to do something with it
                */
                log.debug("Data passed on event: ${data}")

              }
              else {
                log.debug("Not handling event for topic ${record.topic}");
              }
            }
            catch(Exception e) {
              log.error("problem processing event notification",e);
            }
            finally {
              log.debug("Completed processing of servint entry event");
            }
          }
          consumer.commitAsync();
        }
      }
    }
    catch ( Exception e ) {
      log.error("Problem in consumer",e);
    }
    finally {
      consumer.close()
    }
  }

  @javax.annotation.PreDestroy
  private void cleanUp() throws Exception {
    log.info("EventConsumerService::cleanUp");
    running = false;

    // @See https://stackoverflow.com/questions/46581674/how-to-finish-kafka-consumer-safetyis-there-meaning-to-call-threadjoin-inside
    consumer.wakeup();
  }

  @Subscriber('okapi:tenant_list_updated')
  public void onTenantListUpdated(event) {
    log.debug("onTenantListUpdated(${event}) data:${event.data} -- Class is ${event.class.name}");
    tenant_list = event.data
    tenant_list_updated = true;
  }

  // Perhaps we should have a TenantListService or similar?
  public Set getTenantList() {
    return tenant_list
  }

  @Subscriber('okapi:tenant_load_reference')
  public void onTenantLoadReference(final String tenantId, 
                                    final String value, 
                                    final boolean existing_tenant, 
                                    final boolean upgrading, 
                                    final String toVersion, 
                                    final String fromVersion) {
    log.info("onTenantLoadReference(${tenantId},${value},${existing_tenant},${upgrading},${toVersion},${fromVersion})");
  }

}

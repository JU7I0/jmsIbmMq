package com.app.jmsIbmMq;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootApplication
@EnableJms
@RestController
public class JmsIbmMqApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(JmsIbmMqApplication.class);

	@Autowired
	private JmsTemplate jmsTemplate;

	public static void main(String[] args) {
		SpringApplication.run(JmsIbmMqApplication.class, args);
	}

	@GetMapping("send")
	String send(){
		try{
			jmsTemplate.convertAndSend("DEV.QUEUE.1", "Hello World!");
			return "OK";
		}catch(JmsException ex){
			LOGGER.error(ex.getMessage());
			return "FAIL";
		}
	}

	@GetMapping("recv")
	String recv(){
		try{
			return jmsTemplate.receiveAndConvert("DEV.QUEUE.1").toString();
		}catch(JmsException ex){
			LOGGER.error(ex.getMessage());
			return "FAIL";
		}
	}

	@GetMapping("send2")
	public String send2(@RequestParam String string){
		jmsTemplate.setDefaultDestinationName("DEV.QUEUE.1");
		try{
			String correlationId = putMessageOnQueue(jmsTemplate, string);
			return "Ok - correlationId: " + correlationId;
		}catch(JmsException ex){
			LOGGER.error(ex.getMessage());
			return "FAIL";
		}
	}

	public String putMessageOnQueue(JmsTemplate jmsTemplate, String message) {
		final AtomicReference<Message> atomicMsg = new AtomicReference<>();

		// Define o correlationID para a mensagem
		String correlationId = generateCorrelationId();

		jmsTemplate.convertAndSend(Objects.requireNonNull(jmsTemplate.getDefaultDestinationName()), message, new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message message) throws JmsException {
				try {
					// Define o correlationID na mensagem
					message.setJMSCorrelationID(correlationId);
					atomicMsg.set(message);
				} catch (JMSException e) {
					LOGGER.error(e.getMessage());
				}
				return message;
			}
		});

		try {
			// Retorna o correlationID da mensagem
			return correlationId;
		} catch (JmsException e) {
			LOGGER.error(e.getMessage());
			return "FAIL";
		}
	}

	private String generateCorrelationId() {
		// Gera um novo correlationID único
		return UUID.randomUUID().toString();
	}

	@GetMapping("recv2")
	public String recv2(@RequestParam  String correlationId) throws JMSException {
		jmsTemplate.setDefaultDestinationName("DEV.QUEUE.1");
		String message = getMessageOnQueue(jmsTemplate, correlationId);
		if (message != null) {
			LOGGER.error("RECEBEU RESPOSTA DA FILA: "+message);
		}else {
			LOGGER.error("Retorno da message vazia! ");
		}
		LOGGER.error("FIM RECV2 (texto): [" + message + "]");
		return message;
	}

	public static String getMessageOnQueue(JmsTemplate jms, String correlationId) throws JMSException {
		LOGGER.error("Iniciando getMessageOnQueue()...");
		jms.setReceiveTimeout(10000);
		Message m = jms.receiveSelected("JMSCorrelationID = '" + correlationId + "'");
		if (m != null) {
			if (m.isBodyAssignableTo(String.class)) {
				String messageContent = m.getBody(String.class);
				LOGGER.error("String recebida da fila: " + messageContent);
				return messageContent.replace("\u0000", " ").replace("\u0000", " ");
			} else {
				LOGGER.error("O corpo da mensagem não pode ser convertido para string");
				throw new JMSException("O corpo da mensagem não pode ser convertido para string");
			}
		} else {
			LOGGER.error("Nenhuma mensagem recebida da fila para o correlation ID: " + correlationId);
			return null;
		}
	}
}

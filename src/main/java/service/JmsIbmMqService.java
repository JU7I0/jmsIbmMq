package service;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static Utils.Constants.Erros.FALHA_NO_RECEBIMENTO_DA_MENSAGEM;
import static Utils.Constants.Filas.DEV_QUEUE_1;

@Service
public class JmsIbmMqService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsIbmMqService.class);

    private final JmsTemplate jmsTemplate;

    public JmsIbmMqService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public String enviarMsgParaFila() {
        try{
            jmsTemplate.convertAndSend(DEV_QUEUE_1, "Hello World!");
            return "OK";
        }catch(JmsException ex){
            LOGGER.error(ex.getMessage());

            return "FAIL";
        }
    }

    public String receberMsgDaFila() {
        String msg;
        try{
            var message = jmsTemplate.receiveAndConvert(DEV_QUEUE_1);
            if (message != null) {
                msg = message.toString();
            } else {
                msg = "nenhum mensagem recebida na fila";
            }
            return msg;
        }catch(JmsException ex){
            LOGGER.error(ex.getMessage());
            return FALHA_NO_RECEBIMENTO_DA_MENSAGEM;
        }
    }

    public String enviarMsgComCorrelationIdParaFila(String string) {
        jmsTemplate.setDefaultDestinationName(DEV_QUEUE_1);
        try{
            String correlationId = putMessageOnQueue(jmsTemplate, string);
            return "Ok - correlationId: " + correlationId;
        }catch(JmsException ex){
            LOGGER.error(ex.getMessage());
            return FALHA_NO_RECEBIMENTO_DA_MENSAGEM;
        }
    }

    public String putMessageOnQueue(JmsTemplate jmsTemplate, String message) {
        final AtomicReference<Message> atomicMsg = new AtomicReference<>();

        // Define o correlationID para a mensagem
        String correlationId = generateCorrelationId();

        jmsTemplate.convertAndSend(Objects.requireNonNull(jmsTemplate.getDefaultDestinationName()), message, message1 -> {
            try {
                // Define o correlationID na mensagem
                message1.setJMSCorrelationID(correlationId);
                atomicMsg.set(message1);
            } catch (JMSException e) {
                LOGGER.error(e.getMessage());
            }
            return message1;
        });

        try {
            // Retorna o correlationID da mensagem
            return correlationId;
        } catch (JmsException e) {
            LOGGER.error(e.getMessage());
            return FALHA_NO_RECEBIMENTO_DA_MENSAGEM;

        }
    }

    private String generateCorrelationId() {
        // Gera um novo correlationID único
        return UUID.randomUUID().toString();
    }

    public String receberMsgPorCorrelationIdDaFila(String correlationId) throws JMSException {
        jmsTemplate.setDefaultDestinationName(DEV_QUEUE_1);
        String message = getMessageOnQueue(jmsTemplate, correlationId);
        if (message != null) {
            LOGGER.error("RECEBEU RESPOSTA DA FILA: {}",message);
        }else {
            LOGGER.error("Retorno da message vazia! ");
        }
        LOGGER.error("FIM RECV2 (texto): [ {} ]", message);
        return message;
    }

    public String getMessageOnQueue(JmsTemplate jms, String correlationId) throws JMSException {
        LOGGER.error("Iniciando getMessageOnQueue()...");
        jms.setReceiveTimeout(10000);
        Message m = jms.receiveSelected("JMSCorrelationID = '" + correlationId + "'");
        if (m != null) {
            if (m.isBodyAssignableTo(String.class)) {
                String messageContent = m.getBody(String.class);
                LOGGER.error("String recebida da fila: {}", messageContent);
                return messageContent.replace("\u0000", " ").replace("\u0000", " ");
            } else {
                LOGGER.error("O corpo da mensagem não pode ser convertido para string");
                throw new JMSException("O corpo da mensagem não pode ser convertido para string");
            }
        } else {
            LOGGER.error("Nenhuma mensagem recebida da fila para o correlation ID:  {}", correlationId);
            return null;
        }
    }
}

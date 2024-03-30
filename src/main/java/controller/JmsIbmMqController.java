package controller;

import jakarta.jms.JMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.JmsIbmMqService;


@RestController("jms")
public class JmsIbmMqController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsIbmMqController.class);
    private JmsIbmMqService jmsIbmMqService;

    public JmsIbmMqController(JmsIbmMqService jmsIbmMqService) {
        this.jmsIbmMqService = jmsIbmMqService;
    }

    @GetMapping("/send")
    String send(){
        LOGGER.error(" ### INCIO ENVIAR MSG PARA FILA ###");
        return jmsIbmMqService.enviarMsgParaFila();
    }

    @GetMapping("/recv")
    String recv(){
        LOGGER.error(" ### INCIO RECEBER MAG DA FILA ###");
        return jmsIbmMqService.receberMsgDaFila();
    }

    @GetMapping("/send2")
    public String send2(@RequestParam String string){
        LOGGER.error(" ### INCIO ENVIAR MSG PARA FILA COM CORRELATIOID ###");
        return jmsIbmMqService.enviarMsgComCorrelationIdParaFila(string);
    }

    @GetMapping("/recv2")
    public String recv2(@RequestParam  String correlationId) throws JMSException {
        LOGGER.error(" ### INCIO RECEBER MSG da FILA COM CORRELATIOID ###");
        return jmsIbmMqService.receberMsgPorCorrelationIdDaFila(correlationId);
    }


}
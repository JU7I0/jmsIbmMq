# Integração JMS com IBM MQ usando Spring Boot

## Descrição do Projeto
Este repositório contém um projeto Java para integração com IBM MQ utilizando JMS (Java Message Service). A aplicação é uma API RESTful construída com Spring Boot que permite o envio e recebimento de mensagens de uma fila específica no IBM MQ.

## Recursos Principais
- **Envio de Mensagem:** Utilizando o endpoint `/send`, a aplicação permite o envio de uma mensagem para a fila `DEV.QUEUE.1`.
- **Recebimento de Mensagem:** Através do endpoint `/recv`, é possível receber uma mensagem da fila `DEV.QUEUE.1`.
- **Envio de Mensagem com Correlation ID:** O endpoint `/send2` possibilita o envio de uma mensagem para a fila `DEV.QUEUE.1` com um Correlation ID específico.
- **Recebimento de Mensagem por Correlation ID:** O endpoint `/recv2` permite o recebimento de uma mensagem da fila `DEV.QUEUE.1` com base em um Correlation ID específico.

## Recursos Adicionais
- **Gerenciamento de Correlation ID:** A aplicação implementa a geração automática de Correlation IDs únicos para cada mensagem enviada.
- **Tratamento de Erros:** A aplicação realiza o tratamento de exceções durante o envio e recebimento de mensagens, garantindo a robustez do sistema.

Este projeto é útil para desenvolvedores que necessitam integrar suas aplicações Java com IBM MQ utilizando o padrão JMS, proporcionando uma maneira simples e eficiente de enviar e receber mensagens de filas específicas.

# Como Executar o Projeto:

## 1. Configurar o IBM MQ:
 - Siga o tutorial neste link para configurar e iniciar o IBM MQ no Docker: https://developer.ibm.com/components/ibm-mq/tutorials/mq-connect-to-queue-manager-using-docker/ , 
seguindo o tutorial é possivel criar um imagem do IBM MQ no docker para criar filas, canais e fazer consultas via console: https://localhost:9443/ibmmq/console , 
 - Login e Senha estão no arquivo application.properties, para projetos reais é não é indicado o uso de senhas explicitas no arquivo. 
 - Certifique-se de ter o Docker Desktop instalado e em execução.

## 2. Clonar o repositório:
git clone https://github.com/JU7I0/jmsIbmMq

## 3. Abrir o Projeto:
cd jmsIbmMq

## 4. Compilar o Projeto:
mvn clean package

## 5. Executar a Aplicação:
java -jar target/JmsIbmMqApplication.jar

## 6. Acessar a API:
Uma vez que a aplicação esteja em execução, você pode acessar os endpoints da API usando um navegador da web ou ferramentas como cURL ou Postman. Aqui estão os endpoints disponíveis:
 - Envio de Mensagem: http://localhost:8080/send
 - Recebimento de Mensagem: http://localhost:8080/recv
 - Envio de Mensagem com Correlation ID: http://localhost:8080/send2?string=sua-mensagem-aqui
 - Recebimento de Mensagem por Correlation ID: http://localhost:8080/recv2?correlationId=seu-correlation-id

## 7. Interagir com os Endpoints:
Use as URLs fornecidas para interagir com os diferentes endpoints da API conforme necessário. Certifique-se de substituir `sua-mensagem-aqui` e `seu-correlation-id` pelos valores apropriados.

## 8. Encerrar a Aplicação:
Para encerrar a execução da aplicação, pressione `Ctrl + C` no terminal onde a aplicação está sendo executada.

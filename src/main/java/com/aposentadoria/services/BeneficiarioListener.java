package com.aposentadoria.services;

import com.aposentadoria.config.AporteAMQPConfig;
import com.aposentadoria.config.AporteWebSocketConfiguration;
import com.aposentadoria.domain.Usuario;
import com.aposentadoria.domain.dto.AporteDTO;
import com.aposentadoria.domain.enums.SituacaoCalculo;
import com.aposentadoria.repositories.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class BeneficiarioListener {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UsuarioService service;
    private final UsuarioRepository repository;

    public BeneficiarioListener(SimpMessagingTemplate simpMessagingTemplate, UsuarioService service, UsuarioRepository repository) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.service = service;
        this.repository = repository;
    }

    @Transactional
    @RabbitListener(queues = AporteAMQPConfig.QUEUE)
    public void execute(Message message) {
        Long usuarioId = null;
        try {
            AporteDTO aporte = new ObjectMapper().readValue(message.getBody(), AporteDTO.class);

            usuarioId = aporte.getId();
            Usuario result = process(aporte);

            System.out.println("Finalizando cálculo para a pessoa: " + result.getNome());

            //TODO simulando um processo mais lento
            Thread.sleep(1500);

            simpMessagingTemplate.convertAndSend(AporteWebSocketConfiguration.BROKER, new ObjectMapper().writeValueAsString(result));

        } catch (Exception e) {
            if (usuarioId != null) {
                repository.save(Usuario.Builder.from(service.find(usuarioId))
                        .situacao(SituacaoCalculo.ERRO)
                        .build());
            }
            e.printStackTrace();
        }
    }

    private Usuario process(AporteDTO aporte) {
        Usuario usuario = service.find(aporte.getId());

        BigDecimal saldoTotal = usuario.getSaldoTotal().add(aporte.getValor());

        BigDecimal numeroMesesRecebendo = BigDecimal.valueOf(usuario.getNumeroAnosRecebendo()).multiply(BigDecimal.valueOf(12));

        BigDecimal valorAtualRecebimento = saldoTotal.divide(numeroMesesRecebendo, RoundingMode.HALF_DOWN);

        return repository.save(Usuario.Builder.from(usuario)
                .saldoTotal(saldoTotal)
                .valorAtualMensal(valorAtualRecebimento)
                .situacao(SituacaoCalculo.CONCLUIDO)
                .build());
    }

}

package com.aposentadoria.services.utils;

import com.aposentadoria.domain.Usuario;
import com.aposentadoria.domain.enums.Perfil;
import com.aposentadoria.domain.enums.SituacaoCalculo;
import com.aposentadoria.repositories.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;

@Service
public class DBService {

    private final UsuarioRepository repository;

    public DBService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public void instantiateTestDatabase() {
        Usuario usuario = Usuario.Builder.create()
                .nome("Maria de Aparecida")
                .cpf("385.939.530-00")
                .email("maria@gmail.com")
                .senha(new BCryptPasswordEncoder().encode("123"))
                .saldoTotal(BigDecimal.ZERO)
                .valorAtualMensal(BigDecimal.ZERO)
                .perfis(Collections.singleton(Perfil.ADMIN))
                .build();

        repository.save(usuario);
    }
}

package com.aposentadoria.services;

import com.aposentadoria.domain.QUsuario;
import com.aposentadoria.domain.Usuario;
import com.aposentadoria.domain.enums.Perfil;
import com.aposentadoria.domain.exception.AuthorizationException;
import com.aposentadoria.domain.exception.ObjectNotFoundException;
import com.aposentadoria.repositories.UsuarioRepository;
import com.aposentadoria.security.UserSS;
import com.aposentadoria.utils.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario find(Long id) {
        Optional<Usuario> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException(
                "Objeto não encontrado! Id: " + id + ", Tipo: " + Usuario.class.getName()));
    }

    public Usuario findByEmail(String email) {
        UserSS user = UserService.authenticated();
        if (user == null || !user.hasRole(Perfil.ADMIN) && !email.equals(user.getUsername())) {
            throw new AuthorizationException("Acesso negado");
        }

        Optional<Usuario> obj = repository.findOne(QUsuario.usuario.email.eq(email));

        return obj.orElseThrow(() -> new ObjectNotFoundException(
                "Objeto não encontrado! Id: " + user.getId() + ", Tipo: " + Usuario.class.getName()));
    }

    public Usuario update(Usuario usuario, Long id) {
        Usuario entity = find(id);

        Usuario build = Usuario.Builder.from(entity)
                .nome(usuario.getNome())
                .cpf(usuario.getCpf())
                .email(usuario.getEmail())
                .numeroAnosRecebendo(usuario.getNumeroAnosRecebendo())
                .perfis(usuario.getPerfis())
                .build();

        return repository.save(build);
    }
}

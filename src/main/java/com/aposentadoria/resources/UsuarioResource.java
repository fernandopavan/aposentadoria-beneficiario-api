package com.aposentadoria.resources;

import com.aposentadoria.domain.QUsuario;
import com.aposentadoria.domain.Usuario;
import com.aposentadoria.domain.exception.DataIntegrityException;
import com.aposentadoria.repositories.UsuarioRepository;
import com.aposentadoria.services.UsuarioService;
import com.querydsl.core.BooleanBuilder;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping(value = "/usuarios")
public class UsuarioResource {

    private final UsuarioService service;
    private final UsuarioRepository repository;

    public UsuarioResource(UsuarioService service, UsuarioRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @ApiOperation("Busca uma usuário por Id")
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> find(@PathVariable Long id) {
        Usuario obj = service.find(id);
        return ResponseEntity.ok().body(obj);
    }

    @ApiOperation("Busca uma usuário por nome")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/nome")
    public ResponseEntity<Iterable<Usuario>> findByName(@RequestParam(value = "nome") String nome) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (nome != null && !nome.isEmpty()) {
            booleanBuilder.and(QUsuario.usuario.nome.containsIgnoreCase(nome));
        }
        Iterable<Usuario> pessoasFisicas = repository.findAll(booleanBuilder);
        return ResponseEntity.ok().body(pessoasFisicas);
    }

    @ApiOperation("Busca uma usuário por e-mail")
    @GetMapping("/email")
    public ResponseEntity<Usuario> findByEmail(@RequestParam(value = "email") String email) {
        Usuario obj = service.findByEmail(email);
        return ResponseEntity.ok().body(obj);
    }

    @ApiOperation("Insere uma usuário")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Transactional
    @PostMapping
    public ResponseEntity<Usuario> insert(@Valid @RequestBody Usuario usuario) {
        BCryptPasswordEncoder pe = new BCryptPasswordEncoder();
        String senha = pe.encode(usuario.getSenha());
        Usuario build = Usuario.Builder.from(usuario).senha(senha).build();
        return ResponseEntity.ok().body(repository.save(build));
    }

    @ApiOperation("Atualiza uma usuário")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity update(@Valid @RequestBody Usuario usuario, @PathVariable Long id) {
        return ResponseEntity.ok().body(service.update(usuario, id));
    }

    @ApiOperation("Remove uma usuário")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        try {
            if (!repository.exists(QUsuario.usuario.id.ne(id))) {
                throw new DataIntegrityException("Não é possivel excluir a primeiro usuário do sistema :( !");
            }
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityException("Não é possível excluir porque há dados relacionados");
        }

        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Retorna uma lista de pessoas paginada")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retorna a lista de pessoas paginada"),
            @ApiResponse(code = 403, message = "Você não tem permissão para acessar este recurso"),
            @ApiResponse(code = 500, message = "Foi gerada uma exceção"),
    })
    @GetMapping
    public ResponseEntity<Page<Usuario>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "20") Integer limit,
            @RequestParam(value = "orderBy", defaultValue = "nome") String orderBy,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "nome", defaultValue = "") String nome) {

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (nome != null && !nome.isEmpty()) {
            booleanBuilder.and(QUsuario.usuario.nome.containsIgnoreCase(nome));
        }

        PageRequest pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.valueOf(direction), orderBy));

        Page<Usuario> list = repository.findAll(booleanBuilder, pageable);

        return ResponseEntity.ok().body(list);
    }

}

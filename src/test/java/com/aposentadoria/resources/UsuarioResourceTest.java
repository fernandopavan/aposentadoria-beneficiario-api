package com.aposentadoria.resources;

import com.aposentadoria.domain.Usuario;
import com.aposentadoria.domain.enums.Perfil;
import com.aposentadoria.repositories.UsuarioRepository;
import com.aposentadoria.services.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@EnableWebMvc
@SpringBootTest
class UsuarioResourceTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService service;

    @MockBean
    private UsuarioRepository repository;

//    @Test
//    void deveBuscarPeloId() throws Exception {
//        Medico medico = create();
//
//        when(service.find(any())).thenReturn(medico);
//
//        String expected = "{\"createdIn\":null,\"updatedIn\":null,\"version\":null,\"id\":null,\"nome\":\"Nome 1\",\"numeroConselho\":\"1234\",\"estadoConselho\"";
//
//        mvc.perform(get("/medicos/1")
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().string(expected));
//
//        verify(service, times(1)).find(Long.randomUUID());
//    }
//
//    @Test
//    void deveInserir() throws Exception {
//        Medico medico = create();
//        when(repository.save(any(Medico.class))).thenReturn(medico);
//
//        mvc.perform(post("/medicos")
//                .content(objectMapper.writeValueAsString(medico))
//                .accept(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        verify(repository, times(1)).save(eq(medico));
//    }
//
//    @Test
//    void deveAtualizar() throws Exception {
//        Medico medico = create();
//
//        when(repository.findById(Long.randomUUID())).thenReturn(java.util.Optional.ofNullable(medico));
//        when(repository.save(any(Medico.class))).thenReturn(medico);
//
//        String send = "{\"createdIn\":null,\"updatedIn\":null,\"version\":null,\"id\":null,\"nome\":\"Nome 1\",\"numeroConselho\":\"1234\",\"estadoConselho\"";
//
//        mvc.perform(put("/medicos/1")
//                .content(send)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        verify(repository, times(1)).save(any(Medico.class));
//    }
//
//    @Test
//    void deveExcluir() throws Exception {
//        Medico medico = create();
//
//        mvc.perform(delete("/medicos/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNoContent());
//
//        verify(repository, times(1)).deleteById(any());
//    }

    @Test
    void deveRetornarBuscaPaginada() throws Exception {
        PageImpl pagedResponse = new PageImpl(Collections.singletonList(create()));
        when(repository.findAll(any(Predicate.class), any(PageRequest.class))).thenReturn(pagedResponse);

        String expectedContent = "{\"content\":[{\"createdIn\":null,\"updatedIn\":null,\"version\":null,\"id\":null," +
                "\"nome\":\"Nome 1\",\"email\":\"teste@gmail.com\"";

        mvc.perform(get("/medicos")
                .param("page", "0")
                .param("limit", "2")
                .param("orderBy", "nome")
                .param("direction", "DESC")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(expectedContent)));

        verify(repository, times(1)).findAll(any(Predicate.class), any(PageRequest.class));
    }

    private Usuario create() {
        return Usuario.Builder.create()
                .nome("Nome 1")
                .email("teste@gmail.com")
                .senha("123")
                .perfis(Collections.singleton(Perfil.ADMIN))
                .build();
    }

}
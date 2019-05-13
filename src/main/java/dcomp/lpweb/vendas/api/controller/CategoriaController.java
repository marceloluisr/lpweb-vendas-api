package dcomp.lpweb.vendas.api.controller;

import dcomp.lpweb.vendas.api.controller.dto.CategoriaDTO;
import dcomp.lpweb.vendas.api.controller.response.Erro;
import dcomp.lpweb.vendas.api.controller.response.Resposta;
import dcomp.lpweb.vendas.api.model.Categoria;
import dcomp.lpweb.vendas.api.service.CategoriaService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @Autowired
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }


    @GetMapping
    public Resposta<List<CategoriaDTO>> todas() {

        List<Categoria> categorias = categoriaService.todas();
        List<CategoriaDTO> categoriasDTO = new ArrayList<>(categorias.size());

        categorias.forEach(categoria -> categoriasDTO.add(new CategoriaDTO().comDadosDe(categoria)));

        Resposta<List<CategoriaDTO>> resposta = new Resposta<>();
        resposta.setDados(categoriasDTO);

        return resposta;
    }


    @PostMapping
    public ResponseEntity<Resposta<CategoriaDTO>> salva(@Valid @RequestBody CategoriaDTO categoriaDTO) {


        Categoria categoriaSalva = categoriaService.salva(categoriaDTO.getCategoria());

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(categoriaSalva.getId())
                .toUri();

        Resposta<CategoriaDTO> resposta = new Resposta<>();
        resposta.setDados(categoriaDTO.comDadosDe(categoriaSalva));

        return ResponseEntity.created(uri).body(resposta);
    }

    @GetMapping("/{id}")
    public Resposta<CategoriaDTO> buscaPor(@PathVariable Integer id) {

        Categoria categoria = categoriaService.buscaPor(id);

        Resposta<CategoriaDTO> resposta = new Resposta<>();
        resposta.setDados(new CategoriaDTO().comDadosDe(categoria));

        return resposta;
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void exclui(@PathVariable Integer id) {
        categoriaService.excluiPor(id);
    }


 /*   @PutMapping("/{id}")
    public Resposta<CategoriaDTO> altera(@PathVariable  Integer id, @RequestBody CategoriaDTO categoriaDTO) {

        Categoria categoria = categoriaService.buscaPor(id );

        categoria = categoriaDTO.atualizaIgnorandoNuloA(categoria );

        Categoria categoriaAtualizada = categoriaService.atualiza(id, categoria);

        Resposta<CategoriaDTO> resposta = new Resposta<>();
        resposta.setDados(categoriaDTO.comDadosDe(categoriaAtualizada) );

        return resposta;
    }
*/

    @PutMapping("/{id}")
    public ResponseEntity<Resposta<CategoriaDTO>> atualizar(@PathVariable Integer id,
                                                            @RequestBody CategoriaDTO categoriaDTO) {

        Categoria categoria = categoriaService.buscaPor(id);
        categoria = categoriaDTO.atualizaIgnorandoNuloA(categoria);

        Resposta<CategoriaDTO> resposta = new Resposta<>();

        List<Erro> erros = validaProgramaticamente(categoriaDTO.comDadosDe(categoria));

        if (Objects.nonNull( erros ) &&  !erros.isEmpty() ) {
            resposta.setErros(erros );
            return ResponseEntity.badRequest().body(resposta );
        }

        Categoria categoriaAtualizada = categoriaService.atualiza(id, categoria);
        BeanUtils.copyProperties(categoriaAtualizada, categoriaDTO);

        resposta.setDados(new CategoriaDTO().comDadosDe(categoria));

        return ResponseEntity.ok(resposta);

    }

    private List<Erro> validaProgramaticamente(CategoriaDTO categoriaDTO) {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<CategoriaDTO>> validate =
                validator.validate(categoriaDTO);
/*
        validate.forEach (
                error ->{
                    System.out.println("========");
                    System.out.println(error);
                    System.out.println("message= " + error.getMessage() );
                    System.out.println("propertyPath= " + error.getPropertyPath() );
                    System.out.println("value= " + error.getInvalidValue() );
                }
        );
*/
        final List<Erro> erros = new ArrayList<>();

        validate.forEach(violation ->
                erros.add(new Erro(violation.getPropertyPath() + " " + violation.getMessage(),
                        violation.getInvalidValue().toString() + "   " + violation.getMessageTemplate())));
        return erros;
    }
}

package dcomp.lpweb.vendas.api.controller.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dcomp.lpweb.vendas.api.model.Categoria;
import dcomp.lpweb.vendas.api.model.Produto;
import dcomp.lpweb.vendas.api.service.CategoriaService;
import dcomp.lpweb.vendas.api.util.PropriedadesUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.*;

public class ProdutoDTO {

    private Integer id;

    @NotEmpty
    private String nome;

    private BigDecimal precoAtual;

    @NotNull
    private Boolean ativo;

    @JsonProperty("categorias")
    private Set<CategoriaDTO> categoriasDTO = new LinkedHashSet<>();

    @Autowired
    private CategoriaService categoriaService;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPrecoAtual() {
        return precoAtual;
    }

    public void setPrecoAtual(BigDecimal precoAtual) {
        this.precoAtual = precoAtual;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Set<CategoriaDTO> getCategoriasDTO() {
        return categoriasDTO;
    }

    public void setCategoriasDTO(Set<CategoriaDTO> categoriasDTO) {
        this.categoriasDTO = categoriasDTO;
    }

    @JsonIgnore
    public Produto getProduto() {
        Produto produto = new Produto();
        BeanUtils.copyProperties(this, produto);

        if ( Objects.nonNull(this.getCategoriasDTO()) )
            this.getCategoriasDTO().forEach(catDTO -> produto.adiciona(catDTO.getCategoria()) );

        System.out.println("############ -> " + produto );


        return produto;
    }

    public ProdutoDTO comDadosDe(Produto produto) {
        BeanUtils.copyProperties(produto, this);

        if (Objects.nonNull(produto.getCategorias() )
           && Objects.nonNull(this.categoriasDTO)
           && this.categoriasDTO.isEmpty() ) {

                produto.getCategorias()
                        .forEach(cat -> this.categoriasDTO.add(new CategoriaDTO().comDadosDe(cat)) );
        }

        return this;
    }

    public Produto atualizaIgnorandoNuloEm(Produto produto) {

        BeanUtils.copyProperties(this,
                produto,
                PropriedadesUtil.obterPropriedadesComNullDe(this));

        return produto;
    }


    @Override
    public String toString() {
        return "ProdutoDTO{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", precoAtual=" + precoAtual +
                ", ativo=" + ativo +
                ", categoriasDTO=" + categoriasDTO +
                '}';
    }
}
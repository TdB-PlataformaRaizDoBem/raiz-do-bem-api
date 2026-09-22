package br.com.raizdobem.api.repository;

import br.com.raizdobem.api.dto.request.PedidoAjudaUpdateRequest;
import br.com.raizdobem.api.entity.PedidoAjuda;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class PedidoAjudaRepository implements PanacheRepository<PedidoAjuda> {

    public List<PedidoAjuda> listarTodos(){
        return listAll();
    }

    public List<PedidoAjuda> listagemPaginacao(int pagina, int tamanho){
        return findAll().page(Page.of(pagina, tamanho)).list();
    }

    public void criar(PedidoAjuda pedidoAjudaDTO){
        persist(pedidoAjudaDTO);
    }

    public PedidoAjuda buscarPorCpf(String cpf){
        return find("cpf", cpf).firstResult();
    }

    public PedidoAjuda buscarPeloId(Long id) {
        return findById(id);
    }

    public List<PedidoAjuda> listarPorData(LocalDate dataPedido) {
        return list("dataPedido", dataPedido);
    }

    public void atualizar(String cpf, PedidoAjudaUpdateRequest request){
        find("cpf", cpf).firstResult();
//        update("statusPedido = ?1, idDentista = ?2 where cpf = ?3", request.statusPedido(), request.idDentista(), cpf);
    }

    public boolean excluir(Long id) {
        return deleteById(id);
    }
}

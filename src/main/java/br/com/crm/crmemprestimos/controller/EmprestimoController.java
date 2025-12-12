package br.com.crm.crmemprestimos.controller;

import br.com.crm.crmemprestimos.dto.EncerramentoEmprestimoRequest;
import br.com.crm.crmemprestimos.dto.EmprestimoRequest;
import br.com.crm.crmemprestimos.dto.EmprestimoResumoResponse;
import br.com.crm.crmemprestimos.dto.ParcelaPagamentoRequest;
import br.com.crm.crmemprestimos.dto.RenegociacaoEmprestimoRequest;
import br.com.crm.crmemprestimos.model.Cliente;
import br.com.crm.crmemprestimos.model.Emprestimo;
import br.com.crm.crmemprestimos.model.StatusEmprestimo;
import br.com.crm.crmemprestimos.service.ClienteService;
import br.com.crm.crmemprestimos.service.EmprestimoService;
import br.com.crm.crmemprestimos.service.ParcelaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;
    private final ClienteService clienteService;
    private final ParcelaService parcelaService;

    public EmprestimoController(EmprestimoService emprestimoService,
                                ClienteService clienteService,
                                ParcelaService parcelaService) {
        this.emprestimoService = emprestimoService;
        this.clienteService = clienteService;
        this.parcelaService = parcelaService;
    }

    // LISTAR TODOS
    @GetMapping
    public List<Emprestimo> listarTodos() {
        return emprestimoService.listarTodos();
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public Emprestimo buscarPorId(@PathVariable Long id) {
        return emprestimoService.buscarPorId(id);
    }

    // CRIAR
    @PostMapping
    public ResponseEntity<Emprestimo> criar(@Valid @RequestBody EmprestimoRequest request) {
        Cliente cliente = clienteService.buscarPorId(request.getClienteId());

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setCliente(cliente);
        emprestimo.setValorPrincipal(request.getValorPrincipal());
        emprestimo.setTaxaJurosMensal(request.getTaxaJurosMensal());
        emprestimo.setQuantidadeParcelas(request.getQuantidadeParcelas());
        emprestimo.setNumeroParcelas(request.getQuantidadeParcelas());
        emprestimo.setDataContratacao(LocalDate.now());
        emprestimo.setDataPrimeiroVencimento(request.getDataPrimeiroVencimento());
        emprestimo.setObservacoes(request.getObservacoes());
        emprestimo.setSaldoDevedor(request.getValorPrincipal());
        emprestimo.setStatus(StatusEmprestimo.ATIVO);

        Emprestimo emprestimoSalvo = emprestimoService.salvar(emprestimo);

        parcelaService.gerarParcelasParaEmprestimo(emprestimoSalvo);

        return ResponseEntity.status(HttpStatus.CREATED).body(emprestimoSalvo);
    }

    // ATUALIZAR
    @PutMapping("/{id}")
    public Emprestimo atualizar(@PathVariable Long id, @RequestBody Emprestimo dadosAtualizados) {
        return emprestimoService.atualizar(id, dadosAtualizados);
    }

    // DELETAR
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        emprestimoService.deletar(id);
    }

    // ================== ENDPOINTS ESPECIAIS ==================

    // QUITAR
    @PutMapping("/{id}/quitar")
    public Emprestimo quitar(@PathVariable Long id,
                             @RequestBody(required = false) EncerramentoEmprestimoRequest request) {

        String motivo = request != null ? request.getMotivoEncerramento() : null;
        LocalDate dataEncerramento = request != null ? request.getDataEncerramento() : null;

        return emprestimoService.quitarEmprestimo(id, motivo, dataEncerramento);
    }

    // CANCELAR
    @PutMapping("/{id}/cancelar")
    public Emprestimo cancelar(@PathVariable Long id,
                               @RequestBody(required = false) EncerramentoEmprestimoRequest request) {

        String motivo = request != null ? request.getMotivoEncerramento() : null;
        LocalDate dataEncerramento = request != null ? request.getDataEncerramento() : null;

        return emprestimoService.cancelarEmprestimo(id, motivo, dataEncerramento);
    }

    // MARCAR ATRASO
    @PutMapping("/{id}/marcar-atraso")
    public Emprestimo marcarAtraso(@PathVariable Long id) {
        return emprestimoService.marcarAtrasado(id);
    }

    // RENEGOCIAR
    @PutMapping("/{id}/renegociar")
    public Emprestimo renegociar(@PathVariable Long id,
                                 @Valid @RequestBody RenegociacaoEmprestimoRequest request) {

        return emprestimoService.renegociarEmprestimo(
                id,
                request.getNovoValorPrincipal(),
                request.getNovaTaxaJurosMensal(),
                request.getNovaQuantidadeParcelas(),
                request.getNovaDataPrimeiroVencimento(),
                request.getMotivo()
        );
    }

    // RESUMO
    @GetMapping("/{id}/resumo")
    public EmprestimoResumoResponse resumo(@PathVariable Long id) {
        return emprestimoService.resumoDoEmprestimo(id);
    }

    // PAGAR PARCELA POR NÚMERO (rota que você testou no Postman)
    @PostMapping("/{emprestimoId}/parcelas/{numeroParcela}/pagar")
    public ResponseEntity<Void> pagarParcela(@PathVariable Long emprestimoId,
                                             @PathVariable Integer numeroParcela) {
        parcelaService.pagarParcela(emprestimoId, numeroParcela);
        return ResponseEntity.noContent().build();
    }

    // PAGAR PARCELA POR ID (com DTO)
    // POST /emprestimos/parcelas/{parcelaId}/pagamento
    @PostMapping("/parcelas/{parcelaId}/pagamento")
    public ResponseEntity<Void> pagarParcelaPorId(@PathVariable Long parcelaId,
                                                  @RequestBody(required = false) ParcelaPagamentoRequest request) {
        // se request vier null, o service assume pagamento integral com data de hoje
        if (request == null) request = new ParcelaPagamentoRequest();
        parcelaService.registrarPagamento(parcelaId, request);
        return ResponseEntity.noContent().build();
    }

    // ENCERRAR
    @PostMapping("/{id}/encerrar")
    public ResponseEntity<Void> encerrarEmprestimo(@PathVariable Long id,
                                                   @RequestBody(required = false) EncerramentoEmprestimoRequest request) {
        emprestimoService.encerrarEmprestimo(id, request);
        return ResponseEntity.noContent().build();
    }
}

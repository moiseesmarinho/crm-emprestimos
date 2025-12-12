package br.com.crm.crmemprestimos.controller;

import br.com.crm.crmemprestimos.dto.ParcelaPagamentoRequest;
import br.com.crm.crmemprestimos.model.Parcela;
import br.com.crm.crmemprestimos.service.ParcelaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/parcelas")
public class ParcelaController {

    private final ParcelaService parcelaService;

    public ParcelaController(ParcelaService parcelaService) {
        this.parcelaService = parcelaService;
    }

    /**
     * PAGAR PARCELA PELO ID (com body opcional)
     * POST /parcelas/{id}/pagamento
     *
     * Body (opcional):
     * {
     *   "valorPago": 100.00,
     *   "dataPagamento": "2025-12-12",
     *   "observacoes": "Pago em dinheiro"
     * }
     */
    @PostMapping("/{id}/pagamento")
    public ResponseEntity<Parcela> registrarPagamento(
            @PathVariable Long id,
            @RequestBody(required = false) ParcelaPagamentoRequest request
    ) {
        if (request == null) {
            request = new ParcelaPagamentoRequest();
            request.setDataPagamento(LocalDate.now());
            // valorPago null → service assume valorPrevisto
        }

        Parcela atualizada = parcelaService.registrarPagamento(id, request);
        return ResponseEntity.ok(atualizada);
    }

    /**
     * PAGAR PARCELA PELO ID (atalho, sem body)
     * POST /parcelas/{id}/pagar
     */
    @PostMapping("/{id}/pagar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pagarDireto(@PathVariable Long id) {
        ParcelaPagamentoRequest request = new ParcelaPagamentoRequest();
        request.setDataPagamento(LocalDate.now());

        parcelaService.registrarPagamento(id, request);
    }
}

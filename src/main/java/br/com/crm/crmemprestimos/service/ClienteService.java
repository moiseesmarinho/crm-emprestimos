package br.com.crm.crmemprestimos.service;

import br.com.crm.crmemprestimos.model.Cliente;
import br.com.crm.crmemprestimos.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + id));
    }

    public Cliente salvar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente existente = buscarPorId(id);

        existente.setNomeCompleto(clienteAtualizado.getNomeCompleto());
        existente.setCpfCnpj(clienteAtualizado.getCpfCnpj());
        existente.setEmail(clienteAtualizado.getEmail());
        existente.setTelefone(clienteAtualizado.getTelefone());
        existente.setEndereco(clienteAtualizado.getEndereco());
        existente.setObservacoes(clienteAtualizado.getObservacoes());

        return clienteRepository.save(existente);
    }

    public void deletar(Long id) {
        Cliente existente = buscarPorId(id);
        clienteRepository.delete(existente);
    }
}

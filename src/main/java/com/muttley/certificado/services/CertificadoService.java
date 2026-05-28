package com.muttley.certificado.services;

import com.muttley.certificado.model.Certificado;
import com.muttley.certificado.repository.CertificadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CertificadoService {

    @Autowired
    private CertificadoRepository repository;

    public List<Certificado> listarTodos() {
        return repository.findAll();
    }

    public Certificado buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificado não encontrado com id: " + id));
    }

    @Transactional
    public Certificado salvar(Certificado certificado) {
        return repository.save(certificado);
    }

    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Certificado não encontrado com id: " + id);
        }
        repository.deleteById(id);
    }
}

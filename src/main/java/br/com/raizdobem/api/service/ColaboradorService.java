package br.com.raizdobem.api.service;

import br.com.raizdobem.api.repository.ColaboradorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ColaboradorService {
    @Inject
    ColaboradorRepository repository;
}

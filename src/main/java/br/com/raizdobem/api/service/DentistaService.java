package br.com.raizdobem.api.service;

import br.com.raizdobem.api.repository.DentistaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DentistaService {
    @Inject
    DentistaRepository repository;
}

package com.example.graphql.controllers;

import com.example.graphql.entities.Compte;
import com.example.graphql.entities.TypeCompte;
import com.example.graphql.inputs.CompteInput;
import com.example.graphql.repositories.CompteRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@AllArgsConstructor
public class CompteControllerGraphQL {

    private CompteRepository compteRepository;

    @QueryMapping
    public List<Compte> allComptes() {
        return compteRepository.findAll();
    }

    @QueryMapping
    public Compte compteById(@Argument Long id) {
        return compteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Compte %s not found", id)));
    }

    @MutationMapping
    public Compte saveCompte(@Argument("compte") CompteInput compteInput) {
        Compte compte = new Compte();
        compte.setSolde(compteInput.getSolde());
        compte.setDateCreation(compteInput.getDateCreation());

        // Convert the String to TypeCompte
        try {
            compte.setType(TypeCompte.valueOf(compteInput.getType().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(String.format("Invalid type: %s. Allowed values are: %s",
                    compteInput.getType(), List.of(TypeCompte.values())));
        }

        return compteRepository.save(compte);
    }



    @QueryMapping
    public Map<String, Object> totalSolde() {
        long count = compteRepository.count();
        double sum = compteRepository.sumSoldes();
        double average = count > 0 ? sum / count : 0;

        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("sum", sum);
        result.put("average", average);

        return result;
    }
}

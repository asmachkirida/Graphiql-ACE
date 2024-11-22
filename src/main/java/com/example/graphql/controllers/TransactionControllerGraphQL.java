package com.example.graphql.controllers;

import com.example.graphql.entities.Compte;
import com.example.graphql.entities.Transaction;
import com.example.graphql.entities.TypeTransaction;
import com.example.graphql.inputs.TransactionInput;
import com.example.graphql.repositories.CompteRepository;
import com.example.graphql.repositories.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@AllArgsConstructor
public class TransactionControllerGraphQL {

    private  TransactionRepository transactionRepository;
    private  CompteRepository compteRepository;

    @QueryMapping
    public List<Transaction> allTransactions() {
        return transactionRepository.findAll();
    }

    @QueryMapping
    public Transaction transactionById(@Argument Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Transaction with ID %s not found", id)));
    }

    @MutationMapping
    public Transaction addTransaction(@Argument TransactionInput transaction) {
        // Find the associated Compte
        Compte compte = compteRepository.findById(transaction.getCompteId())
                .orElseThrow(() -> new RuntimeException(String.format("Compte with ID %s not found", transaction.getCompteId())));

        Date transactionDate;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            transactionDate = dateFormat.parse(transaction.getDate());
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format. Please use 'yyyy-MM-dd'.", e);
        }

        Transaction newTransaction = new Transaction();
        newTransaction.setMontant(transaction.getMontant());
        newTransaction.setDate(transactionDate);
        newTransaction.setType(transaction.getType());
        newTransaction.setCompte(compte);

        if (transaction.getType() == TypeTransaction.DEPOT) {
            compte.setSolde(compte.getSolde() + transaction.getMontant());
        } else if (transaction.getType() == TypeTransaction.RETRAIT) {
            if (compte.getSolde() < transaction.getMontant()) {
                throw new RuntimeException("Insufficient balance for this withdrawal.");
            }
            compte.setSolde(compte.getSolde() - transaction.getMontant());
        }

        compteRepository.save(compte);
        return transactionRepository.save(newTransaction);
    }
}

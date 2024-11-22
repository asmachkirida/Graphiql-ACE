package com.example.graphql.inputs;

import com.example.graphql.entities.TypeTransaction;
import lombok.Data;

@Data
public class TransactionInput {
    private Long compteId;
    private Double montant;
    private String date;
    private TypeTransaction type;
}

package com.example.cardapiodigital.model;

import com.example.cardapiodigital.helper.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class Entrega {

    private Boolean status = false;

    public Entrega() {
    }

    public static void salvar(List<Entrega> entregaList){
        DatabaseReference entregasRef = FirebaseHelper.getDatabaseReference()
                .child("entregas")
                .child(FirebaseHelper.getIdFirebase());
        entregasRef.setValue(entregaList);
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}

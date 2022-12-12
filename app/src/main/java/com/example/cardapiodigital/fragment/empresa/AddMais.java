package com.example.cardapiodigital.fragment.empresa;

import com.example.cardapiodigital.helper.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class AddMais {
    public static void salvar(List<String> addMaisList){
        DatabaseReference addMaisRef = FirebaseHelper.getDatabaseReference()
                .child("addMais")
                .child(FirebaseHelper.getIdFirebase());
        addMaisRef.setValue(addMaisList);
    }
}

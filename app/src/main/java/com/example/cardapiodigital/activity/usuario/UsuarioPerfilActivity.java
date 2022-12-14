package com.example.cardapiodigital.activity.usuario;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cardapiodigital.R;
import com.example.cardapiodigital.helper.FirebaseHelper;
import com.example.cardapiodigital.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.santalu.maskara.widget.MaskEditText;

public class UsuarioPerfilActivity extends AppCompatActivity {

    private EditText edt_nome;
    private EditText edt_email;

    private ProgressBar progressBar;
    private ImageButton ib_salvar;

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_usuario_perfil);

        iniciaComponentes();

        configCliques();

        recuperaUsuario();

    }

    private void configCliques(){
        findViewById(R.id.ib_voltar).setOnClickListener(v -> finish());
        findViewById(R.id.ib_salvar).setOnClickListener(v -> validaDados());
    }

    private void recuperaUsuario(){
        DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(FirebaseHelper.getIdFirebase());
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    usuario = snapshot.getValue(Usuario.class);
                    configDados();
                }else {
                    configSalvar(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configDados(){
        edt_nome.setText(usuario.getNome());
        edt_email.setText(usuario.getEmail());

        configSalvar(false);
    }

    private void configSalvar(boolean progress){
        if(progress){
            progressBar.setVisibility(View.VISIBLE);
            ib_salvar.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            ib_salvar.setVisibility(View.VISIBLE);
        }
    }

    private void validaDados(){
        String nome = edt_nome.getText().toString().trim();

        if(!nome.isEmpty()){
                configSalvar(true);

                if(usuario == null) usuario = new Usuario();
                usuario.setNome(nome);
                usuario.salvar();

                configSalvar(false);
                Toast.makeText(this, "Dados salvos!", Toast.LENGTH_SHORT).show();
        }else {
            edt_nome.requestFocus();
            edt_nome.setError("Informe um nome para seu cadastro.");
        }
    }

    private void iniciaComponentes(){
        TextView text_toolbar = findViewById(R.id.text_toolbar);
        text_toolbar.setText("Perfil");

        edt_nome = findViewById(R.id.edt_nome);
        edt_email = findViewById(R.id.edt_email);

        progressBar = findViewById(R.id.progressBar);
        ib_salvar = findViewById(R.id.ib_salvar);
    }
}
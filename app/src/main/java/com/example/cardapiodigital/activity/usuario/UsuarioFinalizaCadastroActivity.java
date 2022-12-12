package com.example.cardapiodigital.activity.usuario;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardapiodigital.R;
import com.example.cardapiodigital.model.Login;
import com.example.cardapiodigital.model.Usuario;
import com.santalu.maskara.widget.MaskEditText;

public class UsuarioFinalizaCadastroActivity extends AppCompatActivity {

    private EditText edt_nome;
    private ProgressBar progressBar;

    private Usuario usuario;
    private Login login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_usuario_finaliza_cadastro);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            usuario = (Usuario) bundle.getSerializable("usuario");
            login = (Login) bundle.getSerializable("login");
        }

        iniciaComponentes();

    }

    public void validaDados(View view){
        String nome = edt_nome.getText().toString().trim();

        if(!nome.isEmpty()){
            ocultarTeclado();

            progressBar.setVisibility(View.VISIBLE);

            finalizaCadastro(nome);
        }else {
            edt_nome.requestFocus();
            edt_nome.setError("Informe seu nome.");
        }

    }

    private void finalizaCadastro(String nome){
        login.setAcesso(true);
        login.salvar();

        usuario.setNome(nome);
        usuario.salvar();

        finish();
        startActivity(new Intent(this, UsuarioHomeActivity.class));
    }

    private void iniciaComponentes(){
        edt_nome = findViewById(R.id.edt_nome);
        progressBar = findViewById(R.id.progressBar);
    }

    private void ocultarTeclado(){
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                edt_nome.getWindowToken(), 0
        );
    }
}
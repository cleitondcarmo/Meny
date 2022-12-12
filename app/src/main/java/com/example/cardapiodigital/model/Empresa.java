package com.example.cardapiodigital.model;

import android.net.Uri;

import com.example.cardapiodigital.helper.FirebaseHelper;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Empresa implements Serializable {

    private String id;
    private String nome;
    private String email;
    private String Senha;
    private String descricao;
    private String urlLogo;
    private Boolean acesso;
    private String categoria;
    private Long dataCadastro;

    public Empresa() {
    }

    public void salvar(){
        DatabaseReference empresaRef = FirebaseHelper.getDatabaseReference()
                .child("empresas")
                .child(getId());
        empresaRef.setValue(this);

        FirebaseUser user = FirebaseHelper.getAuth().getCurrentUser();
        UserProfileChangeRequest perfil;
        if(getUrlLogo() == null){
            perfil = new UserProfileChangeRequest.Builder()
                    .setDisplayName(getNome())
                    .build();
        }else {
            perfil = new UserProfileChangeRequest.Builder()
                    .setDisplayName(getNome())
                    .setPhotoUri(Uri.parse(getUrlLogo()))
                    .build();
        }

        if(user != null) user.updateProfile(perfil);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Exclude
    public String getSenha() {
        return Senha;
    }

    public void setSenha(String senha) {
        Senha = senha;
    }

    public String getUrlLogo() { return urlLogo; }

    public void setUrlLogo(String urlLogo) { this.urlLogo = urlLogo; }

    public Boolean getAcesso() {
        return acesso;
    }

    public void setAcesso(Boolean acesso) {
        this.acesso = acesso;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Long getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Long dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}

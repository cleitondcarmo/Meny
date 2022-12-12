package com.example.cardapiodigital.activity.empresa;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cardapiodigital.R;
import com.example.cardapiodigital.helper.FirebaseHelper;
import com.example.cardapiodigital.model.Empresa;
import com.example.cardapiodigital.model.Login;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.IOException;
import java.util.List;

public class EmpresaFinalizaCadastroActivity extends AppCompatActivity {

    private final int REQUEST_GALERIA = 100;
    private ImageView img_logo;
    private EditText edt_nome;
    private EditText edt_categoria;
    private EditText edt_desc;
    private ProgressBar progressBar;
    private String caminhoLogo = "";
    private Empresa empresa;
    private Login login;

    public EmpresaFinalizaCadastroActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_finaliza_cadastro);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            empresa = (Empresa) bundle.getSerializable("empresa");
            login = (Login) bundle.getSerializable("login");
        }

        iniciaComponentes();
    }

    public void selecionarLogo(View view){
        verificaPermissaoGaleria();
    }

    public void validaDados(View view){
        String nome = edt_nome.getText().toString().trim();
        String categoria = edt_categoria.getText().toString().trim();
        String descricao = edt_desc.getText().toString().trim();

        if(!caminhoLogo.isEmpty()){
            if(!nome.isEmpty()){
                if(!categoria.isEmpty()){
                    if(!descricao.isEmpty()) {
                        ocultarTeclado();

                        progressBar.setVisibility(View.VISIBLE);

                        empresa.setNome(nome);
                        empresa.setCategoria(categoria);
                        empresa.setDescricao(descricao);

                        salvarImagemFirebase();
                    }else {
                        edt_desc.requestFocus();
                        edt_desc.setError("Informe uma descrição para seu cadastro.");
                    }
                }else {
                    edt_categoria.requestFocus();
                    edt_categoria.setError("Informe uma categoria para seu cadastro.");
                }
            }
            else {
                edt_nome.requestFocus();
                edt_nome.setError("Informe um nome para seu cadastro.");
            }
        }else {
            progressBar.setVisibility(View.GONE);
            ocultarTeclado();
            erroAutenticacao("Selecione uma logo para seu cadastro.");
        }
    }

    private void verificaPermissaoGaleria(){
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                abrirGaleria();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(EmpresaFinalizaCadastroActivity.this, "Permissão negada.", Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedTitle("Permissão negada.")
                .setDeniedMessage("Se você não aceitar a permissão não poderá acessar a Galeria do dispositivo, deseja ativar a permissão agora ?")
                .setDeniedCloseButtonText("Não")
                .setGotoSettingButtonText("Sim")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    private void abrirGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALERIA);
    }

    private void salvarImagemFirebase(){
        StorageReference storageReference = FirebaseHelper.getStorageReference()
                .child("imagens")
                .child("perfil")
                .child(empresa.getId() + ".JPEG");

        UploadTask uploadTask = storageReference.putFile(Uri.parse(caminhoLogo));
        uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnCompleteListener(task -> {

            login.setAcesso(true);
            login.salvar();

            empresa.setUrlLogo(task.getResult().toString());
            empresa.salvar();

            finish();

            startActivity(new Intent(getBaseContext(), EmpresaHomeActivity.class));

        })).addOnFailureListener(e -> erroAutenticacao(e.getMessage()));
    }

    private void erroAutenticacao(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Atenção");
        builder.setMessage(msg);
        builder.setPositiveButton("OK", ((dialog, which) -> {
            dialog.dismiss();
        }));

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void iniciaComponentes(){
        img_logo = findViewById(R.id.img_logo);
        edt_nome = findViewById(R.id.edt_nome);
        edt_categoria = findViewById(R.id.edt_categoria);
        edt_desc = findViewById(R.id.edt_desc);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_GALERIA){
                Bitmap bitmap;

                Uri imagemSelecionada = data.getData();
                caminhoLogo = data.getData().toString();

                if(Build.VERSION.SDK_INT < 28){
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagemSelecionada);
                        img_logo.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imagemSelecionada);

                    try {
                        bitmap = ImageDecoder.decodeBitmap(source);
                        img_logo.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void ocultarTeclado(){
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
            edt_nome.getWindowToken(), 0
        );
    }
}
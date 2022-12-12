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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.cardapiodigital.R;
import com.example.cardapiodigital.helper.FirebaseHelper;
import com.example.cardapiodigital.model.Empresa;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.santalu.maskara.widget.MaskEditText;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EmpresaConfigActivity extends AppCompatActivity {

    private final int REQUEST_GALERIA = 100;

    private ImageView img_logo;
    private EditText edt_nome;
    private EditText edt_categoria;
    private EditText edt_desc;
    private ProgressBar progressBar;
    private ImageButton ib_salvar;

    private String caminhoLogo;

    private Empresa empresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_config);

        iniciaComponentes();

        recuperaEmpresa();

        configCliques();
    }

    private void configCliques(){
        findViewById(R.id.ib_voltar).setOnClickListener(v -> finish());
        ib_salvar.setOnClickListener(v -> validaDados());
    }

    private void recuperaEmpresa(){
        DatabaseReference empresaRef = FirebaseHelper.getDatabaseReference()
                .child("empresas")
                .child(FirebaseHelper.getIdFirebase());
        empresaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                empresa = snapshot.getValue(Empresa.class);

                configDados();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configDados(){
        Picasso.get().load(empresa.getUrlLogo()).into(img_logo);
        edt_nome.setText(empresa.getNome());
        edt_categoria.setText(empresa.getCategoria());
        edt_desc.setText(empresa.getDescricao());

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

    public void selecionarLogo(View view){
        verificaPermissaoGaleria();
    }

    private void validaDados(){

        String nome = edt_nome.getText().toString().trim();
        String categoria = edt_categoria.getText().toString().trim();
        String descricao = edt_desc.getText().toString().trim();

        if(!nome.isEmpty()){
            if(!descricao.isEmpty()){
                if(!categoria.isEmpty()) {

                    ocultarTeclado();

                    configSalvar(true);

                    empresa.setNome(nome);
                    empresa.setCategoria(categoria);
                    empresa.setDescricao(descricao);

                    if (caminhoLogo != null) {
                        salvarImagemFirebase();
                    } else {
                        empresa.salvar();
                        configSalvar(false);
                        Toast.makeText(this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    edt_categoria.requestFocus();
                    edt_categoria.setError("Informe uma categoria para seu cadastro.");
                }
            }else {
                edt_desc.requestFocus();
                edt_desc.setError("Informe uma descrição para seu cadastro.");
            }
        }else {
            edt_nome.requestFocus();
            edt_nome.setError("Informe um nome para seu cadastro.");
        }
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

            empresa.setUrlLogo(task.getResult().toString());
            empresa.salvar();

            configSalvar(false);
            Toast.makeText(this, "Dados salvos com sucesso!", Toast.LENGTH_SHORT).show();

        })).addOnFailureListener(e -> {
            configSalvar(false);
            erroSalvarDados(e.getMessage());
        });
    }

    private void verificaPermissaoGaleria(){
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                abrirGaleria();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getBaseContext(), "Permissão negada.", Toast.LENGTH_SHORT).show();
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

    private void erroSalvarDados(String msg){
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
        TextView text_toolbar = findViewById(R.id.text_toolbar);
        text_toolbar.setText("Dados da empresa");

        img_logo = findViewById(R.id.img_logo);
        edt_nome = findViewById(R.id.edt_nome);
        edt_desc = findViewById(R.id.edt_desc);
        edt_categoria = findViewById(R.id.edt_categoria);
        progressBar = findViewById(R.id.progressBar);
        ib_salvar = findViewById(R.id.ib_salvar);
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
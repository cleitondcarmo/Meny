package com.example.cardapiodigital.activity.autenticacao;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.cardapiodigital.R;
import com.example.cardapiodigital.adapter.ViewPagerAdapter;
import com.example.cardapiodigital.fragment.empresa.EmpresaFragment;
import com.example.cardapiodigital.fragment.usuario.UsuarioFragment;
import com.google.android.material.tabs.TabLayout;

public class CriarContaActivity extends AppCompatActivity {

    private TabLayout tab_layout;
    private ViewPager view_pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_criar_conta);

        iniciaComponentes();

        configTabsLayout();

        configCliques();

    }

    private void configCliques(){
        findViewById(R.id.ib_voltar).setOnClickListener(v -> finish());
    }

    private void configTabsLayout(){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new UsuarioFragment(), "Usuário");
        viewPagerAdapter.addFragment(new EmpresaFragment(), "Empresa");

        view_pager.setAdapter(viewPagerAdapter);

        tab_layout.setElevation(0);
        tab_layout.setupWithViewPager(view_pager);
    }

    private void iniciaComponentes(){
        TextView text_toolbar = findViewById(R.id.text_toolbar);
        text_toolbar.setText("Cadastra-se");

        tab_layout = findViewById(R.id.tab_layout);
        view_pager = findViewById(R.id.view_pager);
    }

}
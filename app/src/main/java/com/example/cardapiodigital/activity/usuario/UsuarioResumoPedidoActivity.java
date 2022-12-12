package com.example.cardapiodigital.activity.usuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cardapiodigital.DAO.EmpresaDAO;
import com.example.cardapiodigital.DAO.ItemPedidoDAO;
import com.example.cardapiodigital.R;
import com.example.cardapiodigital.adapter.CarrinhoAdapter;
import com.example.cardapiodigital.helper.FirebaseHelper;
import com.example.cardapiodigital.helper.GetMask;
import com.example.cardapiodigital.model.ItemPedido;
import com.example.cardapiodigital.model.Pedido;
import com.example.cardapiodigital.model.StatusPedido;

import java.util.ArrayList;
import java.util.List;

public class UsuarioResumoPedidoActivity extends AppCompatActivity implements CarrinhoAdapter.OnClickListener {

    private List<ItemPedido> itemPedidoList = new ArrayList<>();
    private RecyclerView rv_produtos;
    private CarrinhoAdapter carrinhoAdapter;

    private TextView text_total;

    private ItemPedidoDAO itemPedidoDAO;
    private EmpresaDAO empresaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_usuario_resumo_pedido);

        itemPedidoDAO = new ItemPedidoDAO(this);
        empresaDAO = new EmpresaDAO(this);

        iniciaComponentes();

        configRv();

        configCliques();
    }

    private void configCliques(){
        findViewById(R.id.ib_voltar).setOnClickListener(v -> finish());
        findViewById(R.id.btn_finalizar).setOnClickListener(v -> finalizarPedido());
    }

    private void finalizarPedido(){

        double total = itemPedidoDAO.getTotal();

        Pedido pedido = new Pedido();

        pedido.setIdCliente(FirebaseHelper.getIdFirebase());
        pedido.setIdEmpresa(empresaDAO.getEmpresa().getId());
        pedido.setStatusPedido(StatusPedido.PENDENTE);
        pedido.setItemPedidoList(itemPedidoDAO.getList());
        pedido.setTotalPedido(total);
        pedido.salvar();

        itemPedidoDAO.limparCarrinho();

        Intent intent = new Intent(this, UsuarioHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("id", 3);
        startActivity(intent);

    }

    private void configRv(){
        rv_produtos.setLayoutManager(new LinearLayoutManager(this));
        rv_produtos.setHasFixedSize(true);
        carrinhoAdapter = new CarrinhoAdapter(itemPedidoDAO.getList(), getBaseContext(), this);
        rv_produtos.setAdapter(carrinhoAdapter);
    }

    private void configSaldo(){
        double total = 0;

        if(!itemPedidoDAO.getList().isEmpty()){
            total = itemPedidoDAO.getTotal();
        }

        text_total.setText(getString(R.string.text_valor, GetMask.getValor(total)));
    }

    private void iniciaComponentes(){
        TextView text_toolbar = findViewById(R.id.text_toolbar);
        text_toolbar.setText("Resumo pedido");

        text_total = findViewById(R.id.text_total);
        rv_produtos = findViewById(R.id.rv_produtos);
    }

    @Override
    public void OnClick(ItemPedido itemPedido) {

    }
}
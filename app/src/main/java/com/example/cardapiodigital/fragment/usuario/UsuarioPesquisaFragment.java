package com.example.cardapiodigital.fragment.usuario;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.cardapiodigital.DAO.EmpresaDAO;
import com.example.cardapiodigital.R;
import com.example.cardapiodigital.activity.empresa.EmpresaCardapioActivity;
import com.example.cardapiodigital.adapter.EmpresasAdapter;
import com.example.cardapiodigital.helper.FirebaseHelper;
import com.example.cardapiodigital.model.Empresa;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import eu.livotov.labs.android.camview.ScannerLiveView;
import eu.livotov.labs.android.camview.scanner.decoder.zxing.ZXDecoder;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.VIBRATE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.livotov.labs.android.camview.ScannerLiveView;

public class UsuarioPesquisaFragment extends Fragment implements EmpresasAdapter.OnClickListener{

    private List<Empresa> empresaList = new ArrayList<>();
    private TextView text_info;
    private EmpresaDAO empresaDAO;
    private ScannerLiveView camview;
    private TextView scannerTV;


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_usuario_pesquisa, container, false);
        empresaDAO = new EmpresaDAO(getContext());

        iniciaComponentes(view);


            if (checkPermissao()) {
                text_info.setText("Permitido...");
            } else {
                requestPermissao();
            }

            camview.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener() {
                @Override
                public void onScannerStarted(ScannerLiveView scanner) {
                    text_info.setText("Scaneando");
                }

                @Override
                public void onScannerStopped(ScannerLiveView scanner) {
                    text_info.setText("Scaneamento parado");
                }

                @Override
                public void onScannerError(Throwable err) {
                    text_info.setText("Erro ao scanear QR Code");
                }

                @Override
                public void onCodeScanned(String data) {
                    recuperaEmpresas(data);
                }
            });


            return view;
        }

        private void recuperaEmpresas (String data) {
            DatabaseReference empresaRef = FirebaseHelper.getDatabaseReference().child("empresas");
            empresaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        empresaList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Empresa empresa = ds.getValue(Empresa.class);

                            if (empresa.getId().equals(data)) {
                                empresaList.add(empresa);
                                Intent intent = new Intent(requireActivity(), EmpresaCardapioActivity.class);
                                intent.putExtra("empresaSelecionada", empresa);
                                startActivity(intent);
                            }
                        }
                        text_info.setText("");
                    } else {
                        text_info.setText("Nenhuma empresa cadastrada.");
                    }

                    Collections.reverse(empresaList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        private void iniciaComponentes (View view){
            text_info = view.findViewById(R.id.text_info);
            scannerTV = view.findViewById(R.id.idTVscanned);
            camview = view.findViewById(R.id.camview);
        }

        @Override
        public void OnClick (Empresa empresa){
            Intent intent = new Intent(requireActivity(), EmpresaCardapioActivity.class);
            intent.putExtra("empresaSelecionada", empresa);
            startActivity(intent);
        }

        public void onResume () {
            super.onResume();
            ZXDecoder decoder = new ZXDecoder();
            decoder.setScanAreaPercent(0.8);
            camview.setDecoder(decoder);
            camview.startScanner();
        }

        @Override
        public void onPause () {
            camview.stopScanner();
            super.onPause();
        }

        private boolean checkPermissao() {
            int camera_permissao = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), CAMERA);
            int vibrate_permission = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), VIBRATE);
            return camera_permissao == PackageManager.PERMISSION_GRANTED && vibrate_permission == PackageManager.PERMISSION_GRANTED;
        }

        private void requestPermissao () {
            int PERMISSION_REQUEST_CODE = 200;
            ActivityCompat.requestPermissions(requireActivity(), new String[]{CAMERA, VIBRATE}, PERMISSION_REQUEST_CODE);
        }

        public void onRequestPermissionsResult ( int requestCode, String permissao[],
        int[] grantResults){
            if (grantResults.length > 0) {
                boolean cameraaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean vibrateaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (cameraaccepted && vibrateaccepted) {
                    text_info.setText("Permitido");
                } else {
                    text_info.setText("Não permitido");
                }
            }
        }
    }
package com.leonardomelo.carteiradeclientes;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.leonardomelo.carteiradeclientes.database.DadosOpenHelper;
import com.leonardomelo.carteiradeclientes.dominio.entidades.Cliente;
import com.leonardomelo.carteiradeclientes.dominio.repositorio.ClienteRepositorio;

public class ActCadCliente extends AppCompatActivity {

    private EditText edtNome;
    private EditText edtEndereco;
    private EditText edtTelefone;
    private EditText edtEmail;

    private SQLiteDatabase conexao;
    private DadosOpenHelper dadosOpenHelper;
    private ConstraintLayout layoutContentCadCliente;

    private ClienteRepositorio clienteRepositorio;
    private Cliente cliente;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cad_cliente);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //recuperar referencias
        edtNome     = (EditText)findViewById(R.id.edtNome);
        edtEndereco = (EditText)findViewById(R.id.edtEndereco);
        edtTelefone = (EditText)findViewById(R.id.edtTelefone);
        edtEmail    = (EditText)findViewById(R.id.edtEmail);

        layoutContentCadCliente = (ConstraintLayout)findViewById(R.id.layoutContentCadCliente);

        criarConexao();

        verificaParametro();
    }

    private void verificaParametro(){

        //Bundle - classe que armazena o que foi passado por put.extra()

        Bundle bundle = getIntent().getExtras();

        cliente = new Cliente();

        if ( (bundle != null) && (bundle.containsKey("CLIENTE")) ){

            cliente = (Cliente) bundle.getSerializable("CLIENTE");
            edtNome.setText(cliente.nome);
            edtEndereco.setText(cliente.endereco);
            edtTelefone.setText(cliente.telefone);
            edtEmail.setText(cliente.email);

        }
    }

    private void criarConexao(){

        try {

            dadosOpenHelper = new DadosOpenHelper(this);

            conexao = dadosOpenHelper.getWritableDatabase();

            Snackbar.make(layoutContentCadCliente, R.string.message_conexao_criada_sucesso,Snackbar.LENGTH_LONG).setAction("OK",null).show();

            clienteRepositorio = new ClienteRepositorio(conexao);

        }catch (SQLException ex){

            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle(R.string.title_erro);
            dlg.setMessage(ex.getMessage());
            dlg.setNeutralButton(R.string.action_ok, null);
            dlg.show();

        }

    }

    private void confirmar(){

        if(!validaCampos()){

            try{

                if ( cliente.codigo == 0 ) {
                   clienteRepositorio.inserir(cliente);
                }
                else {
                   clienteRepositorio.alterar(cliente);
                }

                finish();
            }
            catch (SQLException ex){
                AlertDialog.Builder dlg = new AlertDialog.Builder(this);
                dlg.setTitle(R.string.title_erro);
                dlg.setMessage(ex.getMessage());
                dlg.setNeutralButton(R.string.action_ok, null);
                dlg.show();
            }


        }

    }

    private boolean validaCampos(){

        boolean res = false;
        String nome     = edtNome.getText().toString();
        String endereco = edtEndereco.getText().toString();
        String telefone = edtTelefone.getText().toString();
        String email    = edtEmail.getText().toString();

        cliente.nome        = nome;
        cliente.endereco    = endereco;
        cliente.telefone    = telefone;
        cliente.email       = email;

        if (res = isCampoVazio(nome)){
            edtNome.requestFocus();
        }
        else
            if (res = isCampoVazio(endereco)){
                edtEndereco.requestFocus();
            }
            else
                if(res = isCampoVazio(telefone)){
                    edtTelefone.requestFocus();
                }
                else
                    if (res = !isEmailValido(email)){
                    edtEmail.requestFocus();
                    }

        if (res){
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle(R.string.title_aviso);
            dlg.setMessage(R.string.message_campos_invalidos_brancos);
            dlg.setNeutralButton(R.string.action_ok,null);
            dlg.show();
        }

        return res;
    }

    private boolean isCampoVazio(String valor){

        boolean resultado = (TextUtils.isEmpty(valor) || valor.trim().isEmpty());
        return resultado;

    }

    private boolean isEmailValido(String email){

        boolean resultado = (!isCampoVazio(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());

        return resultado;

    }

    //Sobreescreve o método para carregar o menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_act_cad_cliente, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // Trata o item selecionado no menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){

            case android.R.id.home:
                finish();
                break;

            case R.id.action_ok:

                confirmar();
                //Toast.makeText(this, "Botão ok pressionado", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_excluir:

                //Toast.makeText(this, "Botão cancelar pressionado", Toast.LENGTH_SHORT).show();
                clienteRepositorio.excluir(cliente.codigo);
                finish();
                break;

        }



        return super.onOptionsItemSelected(item);
    }
}

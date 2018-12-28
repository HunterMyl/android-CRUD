package com.leonardomelo.carteiradeclientes.dominio.entidades;

import java.io.Serializable;

public class Cliente implements Serializable {

    public int codigo;
    public String nome;
    public String endereco;
    public String telefone;
    public String email;

    public Cliente(){
        codigo = 0;
    }

}

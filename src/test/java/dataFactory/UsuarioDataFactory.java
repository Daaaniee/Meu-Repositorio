package dataFactory;

import Pojo.UsuarioPojo;

public class UsuarioDataFactory {
    public static UsuarioPojo CriarUsuarioAdministrador() {

        UsuarioPojo usuario = new UsuarioPojo();
        usuario.setUsuarioLogin("user");
        usuario.setUsuarioSenha("user");

        return usuario;


    }

}





package com.example.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.whatsapp.R;
import com.example.whatsapp.config.ConfiguracaoFirebase;
import com.example.whatsapp.helper.Base64Custom;
import com.example.whatsapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {
    private TextInputEditText editNome, editEmail, editSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.txtLoginEmail);
        editSenha = findViewById(R.id.txtLoginSenha);
    }

    public void cadastrarUsuario(final Usuario usuario) {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
            usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(CadastroActivity.this,
                            "Sucesso ao cadastrar Usuario!", Toast.LENGTH_SHORT).show();
                    try {
                        String idUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                        usuario.setId(idUsuario);
                        usuario.salvar();
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String excecao = "";
                    try {
                        throw task.getException();
                    } catch ( FirebaseAuthWeakPasswordException e) {
                        excecao = "Digite uma senha mais forte!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        excecao = "Por favor, digite um e-mail válido";
                    } catch (FirebaseAuthUserCollisionException e) {
                        excecao = "Esta conta ja foi cadastrada!";
                    } catch (Exception e) {
                        excecao = "Erro ao cadastrar usuario: " +e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroActivity.this,
                            excecao, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void validarCadastroUsuario(View view) {
        String txtNome = editNome.getText().toString();
        String txtEmail = editEmail.getText().toString();
        String txtSenha = editSenha.getText().toString();

        if (!txtNome.isEmpty()) {
            if (!txtEmail.isEmpty()) {
                if (!txtSenha.isEmpty()) {
                    Usuario usuario = new Usuario();
                    usuario.setNome(txtNome);
                    usuario.setEmail(txtEmail);
                    usuario.setSenha(txtSenha);
                    cadastrarUsuario(usuario);
                } else {
                    Toast.makeText(CadastroActivity.this,
                            "Preencha a Senha!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CadastroActivity.this,
                        "Preencha o Email!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(CadastroActivity.this,
                    "Preencha o Nome!", Toast.LENGTH_SHORT).show();
        }
    }
}
package altamir.android.santander.com.br.bankapp;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import altamir.android.santander.com.br.bankapp.classes.Formatacao;
import altamir.android.santander.com.br.bankapp.BDSqlite.UltimoLogin;
import altamir.android.santander.com.br.bankapp.classes.ValidarCampos;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtuser, edtpassword;
    private Button btnlogin;
    private ProgressBar pblogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Inicializar();

        SQLiteDatabase DB = openOrCreateDatabase("Bankapp.db", Context.MODE_PRIVATE, null);
        UltimoLogin ultimoLogin = new UltimoLogin(this);

        String ultimologin = ultimoLogin.CarregaUltimoLogin();
        edtuser.setText(ultimologin);
        if (!ultimologin.equals("")){
            edtpassword.setFocusable(true);
            edtpassword.requestFocus();
        }

        btnlogin.setOnClickListener(this);
        edtuser.addTextChangedListener(Formatacao.InserirFormatacao(edtuser));
    }

    private void Inicializar(){
        edtuser = findViewById(R.id.edtUser);
        edtpassword = findViewById(R.id.edtPassword);
        btnlogin = findViewById(R.id.btnLogin);
        pblogin = findViewById(R.id.pBarLogin);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                if (VerificaCampos()){
                    Login();
                }
                
                break;
        }
    }

    private boolean VerificaCampos(){

        if (!ValidarCampos.ValidarCampoVazio(edtuser, "Preencha seu usuario com CPF ou E-mail")) {
            return false;
        }

        String user = edtuser.getText().toString();
        String cpf = user;
        cpf = Formatacao.RemoverFormatacao(cpf);
        if (user.length() <= 14 && cpf.matches("[0-9]*")) {
            if (!ValidarCampos.ValidarCPF(edtuser)){
                return false;
            }
        }else{
            if (!ValidarCampos.ValidarEmail(edtuser)){
                return false;
            }
        }
        if (!ValidarCampos.ValidarCampoVazio(edtpassword, "A Senha precisa conter: uma letra Maiúscula, um caractere Especial e um Nr")) {
            return false;
        }
        return ValidarCampos.ValidarPassword(edtpassword);
    }
    //verifica os dados no postman
    private void Login() {
        pblogin.setVisibility(View.VISIBLE);

        final OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "user=test_user&password=Test%401&undefined=");
        Request request = new Request.Builder()
                .url("https://bank-app-test.herokuapp.com/api/login")
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .addHeader("Postman-Token", "f4205bef-7439-4abd-acc7-62194182395d")
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                pblogin.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Erro ao conectar", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                UltimoLogin ultimoLogin = new UltimoLogin(LoginActivity.this);
                                ultimoLogin.Salvar(edtuser.getText().toString());

                                JSONObject json = new JSONObject(myResponse);
                                String nome = json.getJSONObject("userAccount").getString("name");
                                String bankAccount = json.getJSONObject("userAccount").getString("bankAccount");
                                String agency = json.getJSONObject("userAccount").getString("agency");
                                Double balance = Double.parseDouble(json.getJSONObject("userAccount").getString("balance"));

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("nome", nome);
                                bundle.putString("bankAccount", bankAccount);
                                bundle.putString("agency", agency);
                                bundle.putDouble("balance", balance);
                                intent.putExtras(bundle);
                                startActivity(intent);

                                pblogin.setVisibility(View.GONE);

                                finish();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    pblogin.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Erro ao conectar", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
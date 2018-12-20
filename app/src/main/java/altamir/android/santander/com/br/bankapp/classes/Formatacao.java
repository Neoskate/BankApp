package altamir.android.santander.com.br.bankapp.classes;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public abstract class Formatacao {

    // substitui todos os caracteres inseridos do regex para ""
    public static String RemoverFormatacao(String s) {

        return s.replaceAll("[.]", "")
                .replaceAll("[-]", "")
                .replaceAll("[/]", "")
                .replaceAll("[(]", "")
                .replaceAll("[)]", "");
    }

    public static TextWatcher InserirFormatacao(final EditText ediTxt) {

        return new TextWatcher() {

            boolean isUpdating;
            String old = "";
            String form = "";

            public void onTextChanged(CharSequence s, int start, int before,int count) {
                String str = Formatacao.RemoverFormatacao(s.toString());
                String formatacao = "";

                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                if (ediTxt.length()<= 14 && str.matches("[0-9]*")){
                    form="###.###.###-##";
                    int i = 0;

                    for (char m : form.toCharArray()) {
                        if (m != '#' && str.length() > old.length()) {
                            formatacao += m;
                            continue;
                        }
                        try {
                            formatacao += str.charAt(i);
                        } catch (Exception e) {
                            break;
                        }
                        i++;
                    }
                }else {
                    formatacao = s.toString();
                }

                isUpdating = true;
                ediTxt.setText(formatacao);
                ediTxt.setSelection(formatacao.length());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        };
    }
}
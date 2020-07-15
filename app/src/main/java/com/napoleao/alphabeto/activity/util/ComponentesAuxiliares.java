package com.napoleao.alphabeto.activity.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.napoleao.alphabeto.R;
import com.napoleao.alphabeto.activity.MainActivity;
import com.napoleao.alphabeto.config.AppConfig;

public class ComponentesAuxiliares {

    /**
     * Faz a chamada de uma Intent.
     * @param context Activity atual
     * @param classe Activity a ser chamada
     * @param select Inteiro que define o tema dos desafios
     */
    public void invocarIntent(Context context, Class classe, int select, Long idTema){
        Intent it = new Intent(context, classe);
        it.putExtra("tema", select);
        it.putExtra("idTema", idTema);
        context.startActivity(it);
        ((Activity) context).finish();
    }

    /**
     * Define as fonte utilizada no aplicativo, visto que nas versões antigas as mesmas não podem
     * ser definidas no arquivo xml da activity.
     * @param context activity atual
     * @param textView label que receberá a fonte
     */
    public static void definirFonte(Context context, TextView textView){
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/luckiest-guy.ttf");
        textView.setTypeface(font);
    }

    /**
     * Desativa a Activity - impede clique em outro botão.
     * @param activity Activity atual
     */
    public void impedirDuploClique(Activity activity){
       activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    /**
     * Instancia os botões personalizados da interface.
     * @param view View que agrupa os botões
     * @param onClickListener Listener responsável pelos eventos de Click
     * @param botoes Array com todos os ID's dos botões
     */
    public void instanciarBotoes(View view, View.OnClickListener onClickListener, int[] botoes, Context context){
        int i;
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), AppConfig.getInstance(context).getCurrentButtonConfig());
        for(i = 0; i < botoes.length; i++){
            Button btn = view.findViewById(botoes[i]);
            btn.setTypeface(typeface, Typeface.BOLD);
            btn.setAllCaps(AppConfig.getInstance(context).isCurrentButtonCase());
            btn.setOnClickListener(onClickListener);
        }
    }

    /**
     * Desativa momentaneamente o clique dos botões.
     * @param botoes Array com todos os ID's dos botões
     * @param view View que agrupa os botões
     */
    public void desligarBotoes(int[] botoes, View view){
        int i;
        for(i = 0; i < botoes.length; i++){
            Button btn = view.findViewById(botoes[i]);
            btn.setEnabled(false);
        }
    }

    /**
     * Ativa o clique dos botões.
     * @param botoes Array com todos os ID's dos botões
     * @param view View que agrupa os botões
     */
    public void ligarBotoes(int[] botoes, View view){
        int i;
        for(i = 0; i < botoes.length; i++){
            Button btn = view.findViewById(botoes[i]);
            btn.setEnabled(true);
        }
    }

    /**
     * Ativa a vibração do dispositivo ao clicar em determinados botões.
     * @param context Activity atual
     */
    public void vibrar(Context context){
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long milliseconds = 100;
        vibrator.vibrate(milliseconds);
    }

    /**
     * Invoca um AlertDialog perguntando se o jogador deseja sair da partida atual.
     * @param activity Activity atual
     */
    public void exibirConfirmacaoFechar(final Activity activity) {

        AlertDialog.Builder mensagem = new AlertDialog.Builder(activity, R.style.AlertDialogStyle);
        mensagem.setTitle("Confirmação");
        mensagem.setIcon(null);
        mensagem.setMessage("Você voltará para a tela inicial. Deseja sair?");

        mensagem.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast toast = Toast.makeText(activity, "Início", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
                activity.finish();
            }
        });

        mensagem.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast toast = Toast.makeText(activity, "Continuando...", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
            }
        });
        mensagem.show();
    }

    /**
     * Invoca um AlertDialog perguntando se o jogador deseja voltar para a tela de seleção de temas.
     * @param activity Activity atual
     */
    public void exibirConfirmacaoVoltar(final Activity activity) {

        AlertDialog.Builder mensagem = new AlertDialog.Builder(activity, R.style.AlertDialogStyle);
        mensagem.setTitle("Confirmação");
        mensagem.setIcon(null);
        mensagem.setMessage("Você voltará para a seleção de temas. Deseja sair?");

        mensagem.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast toast = Toast.makeText(activity, "Seleção de temas", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
                Intent it = new Intent(activity, MainActivity.class);
                activity.startActivity(it);
                activity.finish();
            }
        });

        mensagem.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast toast = Toast.makeText(activity, "Continuando...", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
            }
        });
        mensagem.show();
    }

}

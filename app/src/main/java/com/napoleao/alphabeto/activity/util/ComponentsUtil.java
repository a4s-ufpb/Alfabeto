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

/**
 * Class responsable for an collection of utils for components.
 */
public class ComponentsUtil {

    /**
     * Build the call for an Intent.
     *
     * @param context   Activity used
     * @param className Activity class name used
     * @param select    Id of the theme
     */
    public void startIntent(Context context, Class className, int select, Long idTema) {
        Intent it = new Intent(context, className);
        it.putExtra("tema", select);
        it.putExtra("idTema", idTema);
        context.startActivity(it);
        ((Activity) context).finish();
    }

    /**
     * Define an font for an TextView.
     *
     * @param context  activity used
     * @param textView textLabel for set font
     */
    public static void setFont(Context context, TextView textView) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/luckiest-guy.ttf");
        textView.setTypeface(font);
    }

    /**
     * Disable the touch in the screen.
     *
     * @param activity Activity used
     */
    public void disableTouchInWindow(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    /**
     * Instantiate all buttons in the screen.
     *
     * @param view            View of group of buttons
     * @param onClickListener Listener of click in buttons
     * @param buttons         Array with id of all buttons
     */
    public void allocAllButtons(View view, View.OnClickListener onClickListener, int[] buttons, Context context) {
        int i;
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), AppConfig.getInstance(context).getCurrentButtonConfig());
        for (i = 0; i < buttons.length; i++) {
            Button btn = view.findViewById(buttons[i]);
            btn.setTypeface(typeface, Typeface.BOLD);
            btn.setAllCaps(AppConfig.getInstance(context).isCurrentButtonCase());
            btn.setOnClickListener(onClickListener);
        }
    }

    /**
     * Disable the click in all buttons.
     *
     * @param buttons Array with id of all buttons
     * @param view    View of group of buttons
     */
    public void disableAllButtons(int[] buttons, View view) {
        int i;
        for (i = 0; i < buttons.length; i++) {
            Button btn = view.findViewById(buttons[i]);
            btn.setEnabled(false);
        }
    }

    /**
     * Enable the click in all buttons.
     *
     * @param buttons Array with id of all buttons
     * @param view    View of group of buttons
     */
    public void enableAllButtons(int[] buttons, View view) {
        int i;
        for (i = 0; i < buttons.length; i++) {
            Button btn = view.findViewById(buttons[i]);
            btn.setEnabled(true);
        }
    }

    /**
     * Enable the vibration of the device when click in some buttons.
     *
     * @param context Activity used
     */
    public void vibrateAction(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long milliseconds = 100;
        vibrator.vibrate(milliseconds);
    }

    /**
     * Call an AlertDialog asking if the player want to exit the current game.
     *
     * @param activity Activity used
     */
    public void showCloseConfirmation(final Activity activity) {

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
     * Call an AlertDialog asking if the player want to back to the theme selection screen.
     *
     * @param activity Activity used
     */
    public void showBackConfirmation(final Activity activity) {

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

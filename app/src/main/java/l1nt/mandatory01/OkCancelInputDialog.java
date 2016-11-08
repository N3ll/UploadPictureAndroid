package l1nt.mandatory01;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

public class OkCancelInputDialog {

    AlertDialog.Builder alert;
    String userInput = "";
    Resources resources;
    EditText input;
    AlertDialog alertDialog;
//	public String getUserInput()
//	{
//		return userInput;
//	}
//	public void setUserInput(String userInput){this.userInput=userInput;};

    public String getUserInput() {
        return input.getText().toString();
    }

    public void setUserInput(String userInput) {
        input.setText(userInput);
    };

    public OkCancelInputDialog(Context context, String title, String message, String defaultInput) {
        alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(message);
        input = new EditText(context);
        input.setText(defaultInput);
        alert.setView(input);

        resources = context.getResources();

        alert.setPositiveButton(resources.getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                userInput = input.getText().toString();
                clickOk();
            }
        });

        alertDialog = alert.create();

    }

    public OkCancelInputDialog(Context context, String title, String message) {
        alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(message);
        input = new EditText(context);
        alert.setView(input);
        resources = context.getResources();


        alert.setPositiveButton(resources.getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                userInput = input.getText().toString();
                clickOk();

            }
        });

        alertDialog = alert.create();

    }




	public boolean isShowing(){
		return alertDialog.isShowing();
	}

    public void dismiss() {
        alertDialog.dismiss();
    }

    protected void clickOk() {

    }


    public void show() {
        alertDialog.show();
    }


}

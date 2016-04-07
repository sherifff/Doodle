package com.example.doodle;
import java.util.Queue;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ToggleButton;
import android.app.Activity;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;


@SuppressLint("NewApi") public class MainActivity extends Activity {

	private DrawView draww;
	private Button newButton;
	private ToggleButton bluetooth;
	private BluetoothConnectionManager bcm;
	private BluetoothDataManager bdm;
	private boolean on = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		draww = (DrawView) findViewById(R.id.drawing);
		LinearLayout lay=(LinearLayout) findViewById(R.id.buttonsLayout);
		draww.setStartY(lay.getHeight());
		newButton = (Button) findViewById(R.id.newButton);
		bluetooth = (ToggleButton) findViewById(R.id.toggle);

		initialize();
		//
		bluetooth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!on) {
					try {
						bcm = new BluetoothConnectionManager();
						BluetoothSocket socket = bcm.connect();
						if (socket.isConnected()) {
							Toast.makeText(MainActivity.this, "Connected",
									Toast.LENGTH_LONG).show();
							bluetooth.setBackgroundResource(R.drawable.on2);
						}
						bdm = new BluetoothDataManager(socket);
					} catch (Exception e) {

					}

				} else {
					try {
						bdm.closeOutputStream();
						bcm.closeSocket();
						Toast.makeText(MainActivity.this, "Not Connect",
								Toast.LENGTH_LONG).show();
						bluetooth.setBackgroundResource(R.drawable.off2);
					} catch (Exception e) {

					}
				}
				on = !on;
			}

		});
		newButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				draww.startNew();
			}
		});
		  Thread loopbuf = new Thread(new MessageLoop());
		  loopbuf.start();
	}
	private  class MessageLoop implements Runnable {
    public void run() {
    	while(true){
    	Queue<byte[]>temp=draww.getBuffer();
    	if(!temp.isEmpty()&&on){
    	bdm.send(draww.pop());
    	try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			 System.out.println("Child thread interrupted! ");
		}
    	}	
    }
    }
	}
   	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initialize() {

		on = false;
		bluetooth.setText(null);
		bluetooth.setTextOff(null);
		bluetooth.setTextOn(null);

	}
}

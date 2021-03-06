package com.queston.task1;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.queston.task1.Login.asynclogin;
import com.queston.task1.library.Httppostaux;

import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;


@SuppressWarnings("unused")
public class MainActivity extends Activity {
	private ListView lv;
	double latitude;
	double longitude;
	double radio = 20;
    ArrayList<String> tasks = new ArrayList<String>();
    ArrayList<String> desc_tasks = new ArrayList<String>();
    Httppostaux post;
    String IP_Server="queston.web44.net";//IP DE NUESTRO PC
    String URL_connect="http://"+IP_Server+"/ws/actualizaQuest.php";
    private ProgressDialog pDialog;
    
    
   //hola
    
    // el onCreate hace el listview y pone el listener para cuando le demos el lugar nos
    //muestre las quests de ese lugar
	@Override
	    public void onCreate(Bundle saveInstanceState) {
	          setContentView(R.layout.activity_main);
	          super.onCreate(saveInstanceState);
	          Intent i = getIntent();
	          TextView t = (TextView) findViewById(R.id.txtLat);
	          String usuario = i.getStringExtra("user");
	          t.setText("Hola  "+usuario+"!");
	      	
	          viewLista();
	         
	        //empieza listview 
	}	         
	 //FIN DE onCreate
	
	
	
	
	//Esto es del menu.. luego lo movemos
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	//FIN MENU

	
	
	//Metodo para el boton de actualizar
	//Hace un async para actualizar desde la abse de datos trayendo las quests cercanas
	public void clickgps(View v){
		GPSTracker gps = new GPSTracker(MainActivity.this);
      	
		if(gps.canGetLocation()){
    	  latitude = gps.getLatitude();
    	  longitude = gps.getLongitude();
      	}
      	else{
    	  gps.showSettingsAlert();
      	}
		
		
		new asyncactualizar().execute(Double.toString(latitude),Double.toString(longitude));

		
		Toast.makeText(getApplicationContext(), "Lat: "+latitude+"\nLon:"+longitude, Toast.LENGTH_SHORT).show();
		viewLista();
		gps.stopSelf();
	}

			    
			    
	    	
	    
		

		
	
	
	//Metodo para logout
	public void cambianombre(View v){
		 final TextView t = (TextView) findViewById(R.id.txtLat);
		 
		 
		 
		 Intent i = new Intent( MainActivity.this, Login.class );
		 finish();
         startActivity(i);
		 
		}
	
	
	
	
	
	

public void viewLista(){
	 lv = (ListView) findViewById(R.id.listView1);
     ArrayAdapter<String> arrayAdapter =      
     new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, tasks);
     lv.setAdapter(arrayAdapter); 
     //fin de listview
     
     final TextView nombre = (TextView) findViewById(R.id.textView3);
     final TextView desc = (TextView) findViewById(R.id.textView4);

     lv.setOnItemClickListener(new OnItemClickListener() {
         @Override
		public void onItemClick(AdapterView<?> parent,View view, int position, long id ) {
        	
            nombre.setText(tasks.get((int)lv.getAdapter().getItemId(position)) );
            desc.setText(desc_tasks.get((int)lv.getAdapter().getItemId(position)) );
            
	          Intent i = new Intent(getApplicationContext(), QuestActivity.class);

        	 
            String nomQuest="Hi";
			i.putExtra("nomQuest",nombre.getText());
			String desQuest="World";
			i.putExtra("desQuest",desc.getText());
			
			
			
			startActivity(i);
			
			
         }
     });
}



public void crearQuest(){
	Intent i = new Intent( MainActivity.this, crearQuest.class );
	 finish();
    startActivity(i);
}




 class asyncactualizar extends AsyncTask< String, String, String >{

	@Override
	protected void onPreExecute() {
    	//para el progress dialog
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Autenticando....");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		String latitud,longitud;
		
		
		
		latitud=(params[0]);
		longitud=(params[1]);
		
		if (obtenquest(latitud,longitud)==true){    		    		
			return "ok"; //login valido
		}else{    		
			return "err"; //login invalido     	          	  
		}
		
	}
	
	
	protected void onPostExecute(String result) {

        pDialog.dismiss();//ocultamos progess dialog.
        Log.e("onPostExecute=",""+result);
	}
	
}

 
 public void obtenquest(double latpost,double lonpost) {

 	/*Creamos un ArrayList del tipo nombre valor para agregar los datos recibidos por los parametros anteriores
 	 * y enviarlo mediante POST a nuestro sistema para relizar la validacion*/ 
 	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
  		
		    		postparameters2send.add(new BasicNameValuePair("latitude",Double.toString(latpost)));
		    		postparameters2send.add(new BasicNameValuePair("longitude",Double.toString(lonpost)));

		   //realizamos una peticion y como respuesta obtenes un array JSON
   		JSONArray jdata=post.getserverdata(postparameters2send, URL_connect);

   		/*como estamos trabajando de manera local el ida y vuelta sera casi inmediato
   		 * para darle un poco realismo decimos que el proceso se pare por unos segundos para poder
   		 * observar el progressdialog
   		 * la podemos eliminar si queremos
   		 */
		    SystemClock.sleep(950);

		    if (jdata!=null && jdata.length() > 0){

	    		JSONObject json_data; //creamos un objeto JSON
				try {
					
					
					json_data = jdata.getJSONObject(0); //leemos el primer segmento en nuestro caso el unico
					logstatus=json_data.getInt("logstatus");//accedemos al valor 


				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		            



		  }	}

}



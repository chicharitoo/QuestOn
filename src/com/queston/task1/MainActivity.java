package com.queston.task1;

import java.util.ArrayList;

import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;


@SuppressWarnings("unused")
public class MainActivity extends Activity {
	private ListView lv;
	double latitude;
	double longitude;
	double radio = 20;
    double lat_rec = 29.083457664651124;
    double lon_rec = -110.96024706959724;
    double lat_pis = 29.0817427726573562;
    double lon_pis = -110.96536070108414;
    double lat_nex = 29.082198920231527;
    double lon_nex = -110.9628863632679;
    double latIcr = 29.0826;
    double lonIcr = -110.963615;
    String local = "";
    int lugar =0;
    ArrayList<String> tasks = new ArrayList<String>();
    ArrayList<String> desc_tasks = new ArrayList<String>();
    
	
    
    // el onCreate hace el listview y pone el listener para cuando le demos el lugar nos
    //muestre las quests de ese lugar
	@Override
	    public void onCreate(Bundle saveInstanceState) {
	          setContentView(R.layout.activity_main);
	          super.onCreate(saveInstanceState);
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
	//Este checa la localizacion GPS y se la manda al metodo de locacion, que es el que te
	//muestra el lugar donde estas
	//Ahorita tenemos 4 lugares: Rectoria, Nextlab, Pista y Fuera de posicion
	//Aqui esta el switch que hace nuestra base de datos y nos muestra el lugar en donde estamos.
	public void clickgps(View v){
		GPSTracker gps = new GPSTracker(MainActivity.this);
      	
		if(gps.canGetLocation()){
    	  latitude = gps.getLatitude();
    	  longitude = gps.getLongitude();
      	}
      	else{
    	  gps.showSettingsAlert();
      	}
		
		
		locacion(latitude,longitude);
		
      	TextView localizacion = (TextView) findViewById(R.id.textView6);
        switch(lugar){
         case 1:
       	  localizacion.setText("Nextlab");
       	  local = "NextLab";
        	 tasks.clear();
        	 desc_tasks.clear();
	         tasks.add("Tiendita de Industrial");
	         tasks.add("Di hola a 5 personas");
	         tasks.add("Brinca");
	         tasks.add("Pegale al mario");
	         
	         desc_tasks.add("Ve a la tiendita de industrial");
	         desc_tasks.add("Busca a 5 personas y diles HOLA!");
	         desc_tasks.add("Da 5 brincos en el lugar donde estas");
	         desc_tasks.add("Es grande, asi que ten cuidado");
	         break;
        
        case 2:
       	 localizacion.setText("Rectoria");
      	  local = "Rectoria";

        	 tasks.clear();
        	 desc_tasks.clear();
	         tasks.add("Foto en Rectoria");
	         tasks.add("Plaza Emiliana de Zubeldia");
	         tasks.add("Locacion de novela");
	         tasks.add("Pegale al Ruben");
	         
	         desc_tasks.add("Tomate una foto en rectoria");
	         desc_tasks.add("Comete un dogo en la plaza");
	         desc_tasks.add("Aprendete el nombre del lugar de la novela");
	         desc_tasks.add("Debe andar cerca.. buscalo!");
	         break;
	         
        case 3:
       	 localizacion.setText("Pista");
      	  local = "Pista";
      	  tasks.clear();
        	 desc_tasks.clear();		        
        	 tasks.add("Vueltas a la pista");
	         tasks.add("Carreras");
	         tasks.add("Lanzamiento de algo");
	         tasks.add("Pegale al Gaspar");
	         
	         desc_tasks.add("Con que des 1 sola vuelta en la pista");
	         desc_tasks.add("Invita a un amigo a correr");
	         desc_tasks.add("Lanza una piedra (o algo) lo mas lejos que puedas");
	         desc_tasks.add("No corre.. alcanzalo!");
	         break;
	         
        case 4:
          	 localizacion.setText("Caffenio");
          	  local = "Caffenio";
          	  tasks.clear();
           	 desc_tasks.clear();		        
           	 tasks.add("Vueltas a la pista");
   	         tasks.add("Carreras");
   	         tasks.add("Lanzamiento de algo");
   	         tasks.add("Pegale al Gaspar");
   	         
   	         desc_tasks.add("Con que des 1 sola vuelta en la pista");
   	         desc_tasks.add("Invita a un amigo a correr");
   	         desc_tasks.add("Lanza una piedra (o algo) lo mas lejos que puedas");
   	         desc_tasks.add("No corre.. alcanzalo!");
   	         break;
	         
       default:
	       	localizacion.setText("Fuera de Posicion");
	       	  local = "Fuera de Posicion";
	       	tasks.clear();
	       	desc_tasks.clear();
	       	tasks.add("No hay quests cerca");
		    desc_tasks.add("No hay quests cerca");
		    break;
		}
        Toast.makeText(getApplicationContext(), "Lat: "+latitude+"\nLon:"+longitude, Toast.LENGTH_SHORT).show();
        viewLista();
        gps.stopSelf();
	}
	//FIN DE clickgps
	
	
	
	
	
	//Este es el metodo de locacion que hace los calculos de distancias y nos devuelve el lugar
	//en el que estamos
	public int locacion(double lat,double lon){
		/*
      	if((Math.pow((latitude-lat_rec),2) + Math.pow((longitude - lon_rec),2)) < Math.pow(radio,2)){
 		    return lugar=2;
 	    }
 	    else if((Math.pow((latitude-lat_pis),2) + Math.pow((longitude - lon_pis),2)) < Math.pow(radio,2)){
 	    	return lugar=3;
 	    }
 	    else if((Math.pow((latitude-lat_nex),2) + Math.pow((longitude - lon_nex),2)) < Math.pow(radio,2)){
 	    	return lugar=1;
 	    }
 	    else{
 	    	return lugar=0;
 	    }
 	    */
		
		Location a = new Location("Current");
		a.setLatitude(lat);
		a.setLongitude(lon);
		
		Location b = new Location("NextLab");
		b.setLatitude(lat_nex);
		b.setLongitude(lon_nex);

		Location c = new Location("Rectoria");
		c.setLatitude(lat_rec);
		c.setLongitude(lon_rec);
		
		Location d = new Location("Pista");
		d.setLatitude(lat_pis);
		d.setLongitude(lon_pis);
		
		Location e = new Location("ICRESOn");
		e.setLatitude(latIcr);
		e.setLongitude(lonIcr);
		
		
		if(a.distanceTo(b)<=radio){
 		    return lugar=1;
 	    }
 	    else if(a.distanceTo(c)<=radio){
 	    	return lugar=2;
 	    }
 	    else if(a.distanceTo(d)<=radio){
 	    	return lugar=3;
 	    }
 	   else if(a.distanceTo(e)<=300){
	    	return lugar=4;
	    }
 	    else{
 	    	return lugar=0;
 	    }
	}
	
	
	
	
	
	//Este metodo es el del boton de cambiar de nombre... luego lo cambiaremos para que
	//se conecte a la base de datos y todo eso...
	public void cambianombre(View v){
		 final TextView cNombre = (TextView) findViewById(R.id.txtLong);
         final EditText input = new EditText(this);
         AlertDialog.Builder alert = new AlertDialog.Builder(this);
         
         alert.setTitle("Cambia Nombre");
         alert.setMessage("Pon tu nombre para cambiarlo!");
         alert.setView(input);

         alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	         @Override
			public void onClick(DialogInterface dialog, int whichButton) {
	           Editable value = input.getText();
	           cNombre.setText(value);
	           }
	         });
	
         alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
	           @Override
			public void onClick(DialogInterface dialog, int whichButton) {
	             // Canceled.
	           }
	         });
         
		 alert.show();
		 InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		 imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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
			
			
			String loca = "";
			i.putExtra("loca", local);
			
			startActivity(i);
			
			
         }
     });
}

}



package com.example.busra.e_faturasorgu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import java.lang.*;

public class MainActivity extends AppCompatActivity {

    EditText vergiNoEdit;
    TextView bulunamadı;
    TextView vknNo,musteriTipi,unvan,durumTipi,musteriSecenekTipi,kayitTarihi,guncellemeTarihi;
    TextView baslik,vknT,mtT,unvanT,dtT,mstT,ktT,gtT;
    Button sorgulaButton;

    TextView ipT;

    TableLayout tabLay; // 8 rows
    TableRow baslikTable;
    TableRow rowVknNo;
    TableRow rowMusteriTip;
    TableRow rowUnvan;
    TableRow rowDurumTip;
    TableRow rowSecenekTip;
    TableRow rowKayit;
    TableRow rowGuncelleme;

    String girilenVergiString;
    String ip; //gidecek ip address

    String bulunamadıS;

    String CidS,vknNoS,musteriTipiS,unvanS,durumTipiS,musteriSecenekTipiS,kayitTarihiS,guncellemeTarihiS; // 8 strings

    private ProgressBar progressBar;
    private int progressDurum=0;
    private Handler handler=new Handler();

    TextView yukleniyorT;

    final String NAMESPACE="...";
    final String URL="...";
    final String SOAP_ACTION="...";
    final String METHOD_NAME="...";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vergiNoEdit=(EditText)findViewById(R.id.girilecekId); //EditText
        sorgulaButton=(Button)findViewById(R.id.sorguButtonId); //Button
        bulunamadı=(TextView)findViewById(R.id.bulunamadiId);//bulunamadı TextView


        vknNo=(TextView)findViewById(R.id.vknNoId);  // 7 textView
        musteriTipi=(TextView)findViewById(R.id.mtId);
        unvan=(TextView)findViewById(R.id.unvanId);
        durumTipi=(TextView)findViewById(R.id.dtId);
        musteriSecenekTipi=(TextView)findViewById(R.id.mstId);
        kayitTarihi=(TextView)findViewById(R.id.ktId);
        guncellemeTarihi=(TextView)findViewById(R.id.gtId);

        baslik=(TextView)findViewById(R.id.baslikId); //Değişmeyen textView'lar
        vknT=(TextView)findViewById(R.id.vknTId);
        mtT=(TextView)findViewById(R.id.mtTId);
        unvanT=(TextView)findViewById(R.id.unvanTId);
        dtT=(TextView)findViewById(R.id.dtTId);
        mstT=(TextView)findViewById(R.id.mstTId);
        ktT=(TextView)findViewById(R.id.ktTId);
        gtT=(TextView)findViewById(R.id.gtTId);

        tabLay=(TableLayout)findViewById(R.id.idTable); //table

        baslikTable=(TableRow)findViewById(R.id.baslikTableId);
        rowVknNo=(TableRow)findViewById(R.id.rowVknid);
        rowMusteriTip=(TableRow)findViewById(R.id.rowMtid);
        rowUnvan=(TableRow)findViewById(R.id.rowUnvan);
        rowDurumTip=(TableRow)findViewById(R.id.rowDtid);
        rowSecenekTip=(TableRow)findViewById(R.id.rowMstid);
        rowKayit=(TableRow)findViewById(R.id.rowKtid);
        rowGuncelleme=(TableRow)findViewById(R.id.rowGtid);

        ipT=(TextView)findViewById(R.id.ipId);

        progressBar=(ProgressBar)findViewById(R.id.progressBarId);
        yukleniyorT=(TextView)findViewById(R.id.yukleniyorTId);

        new getIP().execute();

        sorgulaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                closeKey();

                if ((vergiNoEdit.length()==10)||(vergiNoEdit.length()==11))
                {
                    girilenVergiString=vergiNoEdit.getText().toString().trim(); //editText'e girilen vergi nosunu Strig bir degıskene atıyoruz

                    //new getIP().execute();
                    new sorguFonk().execute(girilenVergiString,ip); //sorguFonk classından nesne üretip çalıştırıyoruz butona basıldıgında
                }
                else
                {
                    tabLay.setVisibility(View.GONE); //table layout u invisible yaptık
                    bulunamadı.setVisibility(View.GONE);//bulunamadı mesajını invisible yapacagız

                    //Toast mesajı pozisyon degıstırme
                    Toast toast=Toast.makeText(getApplicationContext(),"Lütfen 10 veya 11 haneli vergi numarası giriniz.",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL,0,730);
                    toast.show();

                    vibrate();
                }

            }


        });

    }


    private class sorguFonk extends AsyncTask<String,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            tabLay.setVisibility(View.GONE);
            bulunamadı.setVisibility(View.GONE);

            sorgulaButton.setEnabled(false);
            yukleniyorT.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            progressBar();

        }

        @Override
        protected Void doInBackground(String... strings) {

            doInFonk(girilenVergiString);

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressBar.setVisibility(View.GONE);
            yukleniyorT.setVisibility(View.GONE);
            sorgulaButton.setEnabled(true);

            if (bulunamadı.getText()=="")
            {
                bulunamadı.setVisibility(View.GONE);
                tabLay.setVisibility(View.VISIBLE);
            }
            else
            {
                tabLay.setVisibility(View.GONE);
                bulunamadı.setVisibility(View.VISIBLE);
            }

            vibrate();
        }

    }

    public void doInFonk(String girilenVergiString)
    {

        //Servise gidip geldiği zamanlarda calısacak kod blogu
        SoapObject sorgu=new SoapObject(NAMESPACE,METHOD_NAME);

        //tırnak ıcındekıler, web servisteki ilgili  metot ile aynı olmalıdır
        sorgu.addProperty("vknno",girilenVergiString);
        sorgu.addProperty("ip",ip);

        SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet=true; // web servisimizin Microsoft tabanlı oldugunu gostermektedır
        envelope.setOutputSoapObject(sorgu);

        HttpTransportSE transport=new HttpTransportSE(URL);
        transport.debug=true;

        try
        {
            transport.call(SOAP_ACTION,envelope);
            SoapObject cevap = (SoapObject) envelope.getResponse();//SoapObject cevap = (SoapObject) envelope.bodyIn; //geri dönen değer

            if(cevap.toString().equals("anyType{}") || cevap==null) // Eğer girilen değerde bir vergi numarası yok ise
            {
                bulunamadıS=" Bu vergi numarası e-Fatura mükellefi değildir.";
                bulunamadı.setText(bulunamadıS);
            }

            else
            {
                bulunamadı.setText("");

                int count=cevap.getPropertyCount();

                for (int i=0; i<count; i++)
                {
                    SoapObject soap=(SoapObject) cevap.getProperty(i);

                    if (soap.hasProperty("Cid"))
                    {
                        CidS=(soap.getPropertyAsString("Cid"));
                    }

                    if (soap.hasProperty("VknNo"))
                    {
                        vknNoS=(soap.getPropertyAsString("VknNo"));
                        vknNo.setText(vknNoS);
                    }

                    if (soap.hasProperty("MusteriTipi"))
                    {
                        musteriTipiS=(soap.getPropertyAsString("MusteriTipi"));
                        musteriTipi.setText(musteriTipiS);
                    }

                    if (soap.hasProperty("Unvan"))
                    {
                        unvanS=(soap.getPropertyAsString("Unvan"));
                        unvan.setText(unvanS);
                    }

                    if (soap.hasProperty("DurumTipi"))
                    {
                        durumTipiS=(soap.getPropertyAsString("DurumTipi"));
                        durumTipi.setText(durumTipiS);
                    }

                    if (soap.hasProperty("MusteriSecenekTipi"))
                    {
                        musteriSecenekTipiS=(soap.getPropertyAsString("MusteriSecenekTipi"));
                        musteriSecenekTipi.setText(musteriSecenekTipiS);
                    }

                    if (soap.hasProperty("KayitTarihi"))
                    {
                        kayitTarihiS=(soap.getPropertyAsString("KayitTarihi"));
                        kayitTarihi.setText(kayitTarihiS);
                    }

                    if (soap.hasProperty("GuncellemeTarihi"))
                    {
                        guncellemeTarihiS=(soap.getPropertyAsString("GuncellemeTarihi"));
                        guncellemeTarihi.setText(guncellemeTarihiS);
                    }

                }

            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (XmlPullParserException e)
        {
            e.printStackTrace();
        }

    }

    public class getIP extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... voids)
        {
            try
            {
                org.jsoup.nodes.Document doc= Jsoup.connect("https://api.ipify.org/").get();
                ip=doc.text();
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }

            ipT.setText("Sorgulama yaptığınız IP adresi : "+ip);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
        }
    }

    public void progressBar()
    {
        progressDurum=0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressDurum<10)
                {
                    progressDurum +=1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progressDurum);
                        }
                    });

                    try
                    {
                        Thread.sleep(10);
                    }

                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void closeKey() //butona basınca klavyenın kendılınden kapanması ıcın
    {
        InputMethodManager closeK=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        closeK.hideSoftInputFromWindow(vergiNoEdit.getWindowToken(),0);
    }

    public void vibrate()
    {
        Vibrator titresim=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        titresim.vibrate(120);
    }

}
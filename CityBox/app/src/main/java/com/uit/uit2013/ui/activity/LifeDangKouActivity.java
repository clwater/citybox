package com.uit.uit2013.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.twotoasters.jazzylistview.JazzyListView;
import com.twotoasters.jazzylistview.effects.HelixEffect;
import com.uit.uit2013.R;
import com.uit.uit2013.adapter.OrderingAdapter;
import com.uit.uit2013.model.DangKou;
import com.uit.uit2013.model.Restaurant;
import com.uit.uit2013.model.SimpleOrder;
import com.uit.uit2013.utils.db.DKDateCtrl;
import com.uit.uit2013.utils.db.ResDateCtrl;
import com.uit.uit2013.utils.network.DangKouNewWork;


import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by yszsyf on 16/2/13.
 */
public class LifeDangKouActivity  extends Activity implements View.OnClickListener {

    private  String dangkouid_s ,dangkouname;
    private int dangkouid;
    private TextView life_title , back , life_updata , life_history  , settlement;
    public static TextView allprice;
    public static ProgressDialog pr;
    private List<Map<String, Object>> data;
    private Vector<DangKou> dangkou = new Vector<DangKou>();
    public static  Vector<SimpleOrder> order = new Vector<SimpleOrder>();
    public static double all = 0 ;
    private JazzyListView life_dangkou_listview;



    private SimpleAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.life_dangkou);

        Intent intent = getIntent();
        dangkouid_s  = intent.getStringExtra("dangkouid");
        dangkouid = Integer.valueOf(dangkouid_s);
        dangkouid++;
        dangkouid_s = String.valueOf(dangkouid);
        dangkouname = intent.getStringExtra("dangkouname");
        createtitle();
        createlist();

    }

    private void createlist() {
        life_dangkou_listview = (JazzyListView)findViewById(R.id.life_dangkou_listview);
        life_dangkou_listview.setTransitionEffect( new HelixEffect());

        try {
            dangkou = DKDateCtrl.QueryRes(this ,dangkouid_s);
        }catch (Exception exceptione){}

        data = getData();
//        adapter = new SimpleAdapter(this,data,R.layout.item_dangkou,
//                new String[]{"name","price","sub","number","add"},
//                new int[]{R.id.itme_dk_name,R.id.itme_dk_price , R.id.item_dk_sub , R.id.item_dk_number , R.id.item_dk_add});

        OrderingAdapter myadapter = new OrderingAdapter(getBaseContext() , data);

        life_dangkou_listview.setAdapter(myadapter);
        //life_res_listview.setOnItemClickListener(this);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i=0 ; i<dangkou.size() ; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", ""+dangkou.get(i).getName().toString());
            map.put("price", "" + dangkou.get(i).getPrice().toString());
            map.put("sub","-");
            map.put("number","0");
            map.put("add","+");
            list.add(map);
        }
        return list;
    }

    private void createtitle() {
        life_title = (TextView) findViewById(R.id.life_title);
        life_title.setText(dangkouname);
        back = (TextView) findViewById(R.id.life_back);
        back.setOnClickListener(this);
        life_updata = (TextView) findViewById(R.id.life_updata);
        life_updata.setOnClickListener(this);
        life_history = (TextView) findViewById(R.id.life_history);
        life_history.setOnClickListener(this);
        allprice = (TextView)findViewById(R.id.allprice);
        settlement = (TextView)findViewById(R.id.settlement);
        settlement.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.life_back:
                this.finish();
                break;
            case R.id.life_history:
                break;
            case R.id.life_updata:
                updata();
                break;
            case R.id.settlement:
                break;
        }
    }

    private void updata()  {
        pr = ProgressDialog.show(LifeDangKouActivity.this, null, "获取食堂数据中......");
        DKDateCtrl.delete(this , dangkouid_s);
        CountingTask task=new CountingTask();
        task.execute();
    }
    private class CountingTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {

            try {
                DangKouNewWork.getDangKou(getApplicationContext(), dangkouid_s);
            } catch (JSONException e) {}
            return null;
        }
        protected void onProgressUpdate(Void... progress){}
        protected void onPostExecute(Void result){
            getdateforwebsuccess();
        }
    }
    private  void getdateforwebsuccess(){
        pr.dismiss();
        dangkou = DKDateCtrl.QueryRes(this , dangkouid_s);

        createlist();

    }


    public static void changeorde(){
        all = 0 ;

        for (int i = 0 ; i < order.size() ; i++){

            double pr = Double.valueOf( order.get(i).getPrice().substring(0 ,order.get(i).getPrice().length() -1 ));
            int nu = order.get(i).getNumber();
            all += ( pr * nu );
        }

        allprice.setText(""+all);
    }





}

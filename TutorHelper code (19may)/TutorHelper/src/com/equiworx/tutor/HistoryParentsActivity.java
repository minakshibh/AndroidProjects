package com.equiworx.tutor;


import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.equiworx.model.StudentList;
import com.equiworx.tutorhelper.R;

public class HistoryParentsActivity extends Activity{
	private ListView listview;
	private HistoryAdapter adapter;
	private ImageView back;
	private ArrayList<String> historydetail=new ArrayList<String>();
	
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		
		intializelayout();
		fetchHistoryDetails();
		setOnClickListners();
	}
	private void setOnClickListners() {
		back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
					finish();
					
				}
			});
		
		listview.setOnItemClickListener(new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			Intent i=new Intent(HistoryParentsActivity.this,HistoryDetails.class);
			startActivity(i);
				
			}
		});
		
	}
	private void fetchHistoryDetails() {
		adapter = new HistoryAdapter(HistoryParentsActivity.this);
		listview.setAdapter(adapter);
		
	}
	private void intializelayout() {
		historydetail.clear();
		back=(ImageView)findViewById(R.id.back);
		listview=(ListView)findViewById(R.id.listview);
		historydetail.add("1");
		historydetail.add("1");
		historydetail.add("1");
		
	}
	public class HistoryAdapter extends BaseAdapter
	{			
		private Context context;
		private TextView tv_name, tv_studentid,tv_parentid,tv_contact,tv_c;
		
		private StudentList studentlist;
		
		public HistoryAdapter(Context ctx)
		{
			context = ctx;
		}

		public int getCount() {
		return historydetail.size();
		}

		public Object getItem(int position) {
		return historydetail.get(position);
		}
		public long getItemId(int position) {
		return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			if(convertView == null){
			    convertView = inflater.inflate(R.layout.history_row, parent, false);
			}

			//studentlist = historydetail.get(position);
					
			tv_name = (TextView)convertView.findViewById(R.id.name);
			tv_studentid = (TextView)convertView.findViewById(R.id.a);
			tv_parentid = (TextView)convertView.findViewById(R.id.b);
			tv_c = (TextView)convertView.findViewById(R.id.c);
			tv_contact= (TextView)convertView.findViewById(R.id.d);
			
			tv_name.setText(":name");
			tv_studentid.setText(":30");
			tv_parentid.setText(":$5465");
			tv_c.setText(":$65416");
			tv_contact.setText(": $51456");
		
			return convertView;
		}
	}

}
